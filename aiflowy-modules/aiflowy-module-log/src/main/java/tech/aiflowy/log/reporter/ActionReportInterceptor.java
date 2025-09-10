package tech.aiflowy.log.reporter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.log.annotation.LogReporterDisabled;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ActionReportInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("ACTION_REPORT");

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        SDF.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 北京时间
    }

    private static final String DIVIDER = "─────────────────────────────────────────────────────────────────────────────";
    private static final int MAX_JSON_LENGTH = 512;

    private static final List<String> SENSITIVE_KEYS = Arrays.asList("password", "passwd", "secret", "token", "key");

    private final ActionLogReporterProperties logProperties;

    public ActionReportInterceptor(ActionLogReporterProperties logProperties) {
        this.logProperties = logProperties;
    }

    // 存储是否需要记录日志的标志
    private static final ThreadLocal<Boolean> SHOULD_LOG = ThreadLocal.withInitial(() -> false);
    // 存储开始时间
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();
    // 存储 HandlerMethod，供 afterCompletion 使用
    private static final ThreadLocal<HandlerMethod> HANDLER_METHOD = new ThreadLocal<>();
    // 存储 ModelAndView，供 afterCompletion 使用
    private static final ThreadLocal<ModelAndView> MODEL_AND_VIEW = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 默认不记录
        SHOULD_LOG.set(false);

        if (!logProperties.isEnabled() || !(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod hm = (HandlerMethod) handler;
        Method method = hm.getMethod();

        // 采样
        if (logProperties.getSampleRate() < 1.0 && Math.random() > logProperties.getSampleRate()) {
            return true;
        }

        // 排除 LogReporterDisabled 注解
        if (method.isAnnotationPresent(LogReporterDisabled.class) ||
                method.getDeclaringClass().isAnnotationPresent(LogReporterDisabled.class)) {
            return true;
        }

        // 设置标志：需要记录日志
        SHOULD_LOG.set(true);
        START_TIME.set(System.currentTimeMillis());
        HANDLER_METHOD.set(hm);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 仅暂存 modelAndView，供 afterCompletion 使用
        if (SHOULD_LOG.get()) {
            MODEL_AND_VIEW.set(modelAndView);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (!Boolean.TRUE.equals(SHOULD_LOG.get())) {
                return;
            }

            HandlerMethod hm = HANDLER_METHOD.get();
            if (hm == null) return;

            Method method = hm.getMethod();
            ModelAndView modelAndView = MODEL_AND_VIEW.get();

            StringBuilder sb = new StringBuilder(2048);
            sb.append("\n").append(DIVIDER).append('\n');

            String timestamp = SDF.format(new Date());
            sb.append("AIFlowy action report -------- ").append(timestamp).append(" -------------------------\n");
            sb.append("Request     : ").append(request.getMethod())
                    .append(" ").append(request.getRequestURI()).append("\n");

            // 打印参数（GET / POST 表单参数），脱敏
            Map<String, String[]> params = request.getParameterMap();
            if (!params.isEmpty()) {
                Map<String, Object> maskedParams = new LinkedHashMap<>();
                for (Map.Entry<String, String[]> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String[] values = entry.getValue();
                    if (values != null && values.length == 1) {
                        maskedParams.put(key, isSensitive(key) ? "***" : values[0]);
                    } else {
                        maskedParams.put(key, isSensitive(key) ? "***" : values);
                    }
                }
                sb.append("Params      : ").append(JSON.toJSONString(maskedParams)).append("\n");
            }

            // ====== 读取 POST Body ======
            String methodStr = request.getMethod();
            if ("POST".equalsIgnoreCase(methodStr) || "PUT".equalsIgnoreCase(methodStr) || "PATCH".equalsIgnoreCase(methodStr)) {
                String body = RequestUtil.readBodyString(request);
                if (body != null && !body.trim().isEmpty()) {
                    try {
                        Object obj = JSON.parse(body);
                        if (obj instanceof Map) {
                            obj = maskSensitiveValues((Map<String, Object>) obj);
                        }
                        String maskedBody = JSON.toJSONString(obj);
                        if (maskedBody.length() > MAX_JSON_LENGTH) {
                            maskedBody = maskedBody.substring(0, MAX_JSON_LENGTH) + " ... (truncated)";
                        }
                        sb.append("Body        : ").append(maskedBody.replace("\n", " ")).append("\n");
                    } catch (Exception e) {
                        String truncated = body.length() > 200 ? body.substring(0, 200) + "..." : body;
                        sb.append("Body        : ").append(truncated).append("\n");
                    }
                }
            }

            // Controller & Method 位置（含行号）
            Class<?> clazz = method.getDeclaringClass();
            String fileName = clazz.getSimpleName() + ".java";
            int lineNumber = JavassistLineNumUtils.getLineNumber(method);
            String lineStr = lineNumber > 0 ? String.valueOf(lineNumber) : "?";
            String controllerLocation = clazz.getName() + ".(" + fileName + ":" + lineStr + ")";
            sb.append("Controller  : ").append(controllerLocation).append("\n");

            // 构建方法签名
            sb.append("Method      : ").append(buildMethodSignature(method));

            // ====== 处理 ModelAndView ======
            if (modelAndView != null && modelAndView.getViewName() != null) {
                sb.append('\n').append("Render      : ").append(modelAndView.getViewName());

                Map<String, Object> model = modelAndView.getModel();
                if (!model.isEmpty()) {
                    sb.append("\nModel       : ");
                    try {
                        String json = JSON.toJSONString(maskSensitiveValues(model), JSONWriter.Feature.WriteMapNullValue);
                        if (json.length() > MAX_JSON_LENGTH) {
                            json = json.substring(0, MAX_JSON_LENGTH) + " ... (truncated)";
                        }
                        sb.append(json.replace("\n", " "));
                    } catch (Exception e) {
                        sb.append("(failed to serialize)");
                    }
                }
            } else {
                sb.append('\n').append("Render      : (none)");
            }

            // ====== 尝试获取最终响应体 ======
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                byte[] body = wrapper.getContentAsByteArray();
                if (body.length > 0) {
                    String content = new String(body, 0, Math.min(body.length, 1024), StandardCharsets.UTF_8);
                    try {
                        Object obj = JSON.parse(content);
                        if (obj instanceof Map) {
                            obj = maskSensitiveValues((Map<String, Object>) obj);
                        }
                        String masked = JSON.toJSONString(obj);
                        if (masked.length() > MAX_JSON_LENGTH) {
                            masked = masked.substring(0, MAX_JSON_LENGTH) + " ... (truncated)";
                        }
                        sb.append('\n').append("Response    : ").append(masked.replace("\n", " "));
                    } catch (Exception e) {
                        String truncated = content.length() > 200 ? content.substring(0, 200) + "..." : content;
                        sb.append('\n').append("Response    : ").append(truncated);
                    }
                }
            }

            // ====== 异常信息 ======
            if (ex != null) {
                sb.append('\n')
                        .append("Status      : FAILED\n")
                        .append("Exception   : ").append(ex.getClass().getSimpleName())
                        .append(": ").append(ex.getMessage() != null ? ex.getMessage().split("\n")[0] : "Unknown");
            }

            // ====== 耗时 ======
            Long start = START_TIME.get();
            long took = start != null ? System.currentTimeMillis() - start : -1;
            if (took >= 0) {
                sb.append('\n')
                        .append("----------------------------------- took ").append(took).append(" ms --------------------------------");
            } else {
                sb.append('\n').append("----------------------------------- took ? ms --------------------------------");
            }

            sb.append('\n').append(DIVIDER);

            logger.info(sb.toString());

        } finally {
            // 清理 ThreadLocal
            SHOULD_LOG.remove();
            START_TIME.remove();
            HANDLER_METHOD.remove();
            MODEL_AND_VIEW.remove();
        }
    }

    // ====================== 工具方法 ======================

    private boolean isSensitive(String key) {
        return SENSITIVE_KEYS.stream().anyMatch(s -> key.toLowerCase().contains(s));
    }

    private Map<String, Object> maskSensitiveValues(Map<String, Object> model) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (isSensitive(key)) {
                result.put(key, "***");
            } else if (value instanceof Map) {
                result.put(key, maskSensitiveValues((Map<String, Object>) value));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 构建方法签名：methodName(paramType paramName, ...)
     */
    private String buildMethodSignature(Method method) {
        StringBuilder sig = new StringBuilder();
        sig.append(method.getName()).append("(");

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            sig.append(param.getType().getSimpleName())
                    .append(" ")
                    .append(param.getName());

            if (i < parameters.length - 1) {
                sig.append(", ");
            }
        }
        sig.append(")");
        return sig.toString();
    }
}