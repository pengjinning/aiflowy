package tech.aiflowy.auth.config;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.aiflowy.common.annotation.UsePermission;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CurdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String groupName = "";
        // 检查handler是否是HandlerMethod类型
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 获取类上的特定注解
            UsePermission classAnnotation = handlerMethod.getBeanType().getAnnotation(UsePermission.class);
            if (classAnnotation != null) {
                // 处理注解逻辑
                groupName = classAnnotation.moduleName();
            }
            // 有此注解，交给 sa token 自行判断
            SaCheckPermission saCheckPermission = handlerMethod.getMethodAnnotation(SaCheckPermission.class);
            if (saCheckPermission != null) {
                return true;
            }
        }
        String requestUri = request.getRequestURI();
        // 查询
        String finalGroupName = groupName;
        SaRouter.match("/**/list",
                "/**/page",
                "/**/detail",
                "/**/intelligentFilling"
        ).check(r -> {
            checkBaseCurd(requestUri, finalGroupName, "query");
        });
        // 保存
        SaRouter.match("/**/save",
                "/**/update"
        ).check(r -> {
            checkBaseCurd(requestUri, finalGroupName, "save");
        });
        // 删除
        SaRouter.match("/**/remove",
                "/**/removeBatch"
        ).check(r -> {
            checkBaseCurd(requestUri, finalGroupName, "remove");
        });

        return true;
    }

    private void checkBaseCurd(String uri, String groupName, String permission) {
        int idx = uri.lastIndexOf("/");
        String per = uri.substring(0,idx + 1) + permission;
        if (StrUtil.isNotEmpty(groupName)) {
            // 如果指定了继承的模块，就改为该模块的权限校验
            per = groupName + "/" + permission;
        }
        StpUtil.checkPermission(per);
    }
}
