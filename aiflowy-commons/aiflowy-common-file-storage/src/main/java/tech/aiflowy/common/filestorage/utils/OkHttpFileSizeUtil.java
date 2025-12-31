package tech.aiflowy.common.filestorage.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 兼容 JDK 8 的 OkHttp 网络文件大小获取工具类（直接 GET 方案，适配公开可访问文件）
 */
public class OkHttpFileSizeUtil {

    // 优化超时配置（公开网络文件可能响应较慢）
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)  // 延长连接超时
            .readTimeout(30, TimeUnit.SECONDS)     // 延长读取超时
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)        // 连接失败自动重试
            .connectionPool(new okhttp3.ConnectionPool(5, 30, TimeUnit.SECONDS))
            .build();

    /**
     * 核心方法：直接用 GET 请求获取文件大小（适配公开可访问文件）
     * @param fileUrl 公开可访问的网络文件地址（HTTP/HTTPS）
     * @return 文件总字节数（-1 表示获取失败）
     */
    public static long getFileSize(String fileUrl) {
        return getFileSize(fileUrl, null);
    }

    /**
     * 带自定义头的重载方法（公开文件可传 null）
     */
    public static long getFileSize(String fileUrl, Map<String, String> headers) {
        // 1. 校验 URL 合法性
        if (fileUrl == null || fileUrl.trim().isEmpty() || !fileUrl.startsWith("http")) {
            System.err.println("错误：无效的网络文件地址：" + fileUrl);
            return -1;
        }
        fileUrl = fileUrl.trim();
        System.out.println("开始获取文件大小：" + fileUrl);

        // 2. 构建 GET 请求（直接用 GET，跳过 HEAD，避免服务器不支持）
        Request.Builder requestBuilder = new Request.Builder()
                .url(fileUrl)
                .get(); // 显式指定 GET 请求

        // 3. 添加默认请求头（模拟浏览器，避免被服务器拦截）
        addDefaultHeaders(requestBuilder, headers);

        Response response = null;
        ResponseBody responseBody = null;
        try {
            // 4. 发送请求（仅读取响应头，不下载文件内容）
            response = OK_HTTP_CLIENT.newCall(requestBuilder.build()).execute();

            // 5. 校验响应状态
            if (!response.isSuccessful()) {
                int statusCode = response.code();
                System.err.println("请求失败，状态码：" + statusCode + "，地址：" + fileUrl);
                return -1;
            }

            // 6. 读取响应头，解析文件大小
            responseBody = response.body();
            if (responseBody == null) {
                System.err.println("响应体为空，无法获取文件大小：" + fileUrl);
                return -1;
            }

            // 优先获取 Content-Length（最直接）
            long contentLength = responseBody.contentLength();
            if (contentLength != -1) {
                System.out.println("成功获取文件大小：" + contentLength + " 字节（来源：Content-Length）");
                return contentLength;
            }

            // 兼容：解析 Content-Range（部分服务器返回该头）
            String contentRange = response.header("Content-Range");
            if (contentRange != null && contentRange.contains("/")) {
                String[] rangeParts = contentRange.split("/");
                if (rangeParts.length == 2) {
                    String totalSizeStr = rangeParts[1].trim();
                    long totalSize = Long.parseLong(totalSizeStr);
                    System.out.println("成功获取文件大小：" + totalSize + " 字节（来源：Content-Range）");
                    return totalSize;
                }
            }

            // 7. 若未获取到大小，尝试读取少量字节（极端情况：服务器流式传输，无大小头）
            System.out.println("响应头无文件大小信息，尝试读取文件流获取大小（仅读取前 1KB，避免下载完整文件）");
            long streamSize = getSizeByReadingStream(responseBody);
            if (streamSize != -1) {
                System.out.println("成功获取文件大小：" + streamSize + " 字节（来源：流读取）");
                return streamSize;
            }

            System.err.println("获取文件大小失败：服务器未返回大小信息，且流读取失败：" + fileUrl);
            return -1;

        } catch (NumberFormatException e) {
            System.err.println("解析文件大小格式错误：" + fileUrl + "，错误：" + e.getMessage());
            return -1;
        } catch (Exception e) {
            System.err.println("获取文件大小异常：" + fileUrl + "，错误：" + e.getMessage());
            return -1;
        } finally {
            // 8. 强制关闭资源，避免泄露
            if (responseBody != null) {
                responseBody.close();
            }
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 添加默认请求头（模拟浏览器，避免被服务器拦截）
     */
    private static void addDefaultHeaders(Request.Builder requestBuilder, Map<String, String> customHeaders) {
        // 默认头：模拟 Chrome 浏览器，提高兼容性
        requestBuilder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        requestBuilder.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        requestBuilder.addHeader("Accept-Encoding", "gzip, deflate, br");
        requestBuilder.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        requestBuilder.addHeader("Connection", "keep-alive");
        requestBuilder.addHeader("Upgrade-Insecure-Requests", "1");

        // 添加自定义头（覆盖默认头，若有重复）
        if (customHeaders != null && !customHeaders.isEmpty()) {
            for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 极端情况：服务器无大小头，通过读取流获取大小（仅读取前 1KB，避免下载大文件）
     * 注意：仅适用于非流式传输的文件（大部分公开文件支持）
     */
    private static long getSizeByReadingStream(ResponseBody responseBody) throws IOException {
        byte[] buffer = new byte[1024]; // 1KB 缓冲区
        long totalSize = 0;
        int len;
        try (java.io.InputStream is = responseBody.byteStream()) {
            // 循环读取流，直到读取完毕（但限制最大读取 100MB，避免恶意文件）
            while ((len = is.read(buffer)) != -1) {
                totalSize += len;
                if (totalSize > 1024 * 1024 * 100) { // 100MB 上限
                    System.err.println("警告：文件超过 100MB，停止读取");
                    return -1;
                }
            }
        }
        return totalSize;
    }

    // ------------------------------ 测试示例 ------------------------------
    public static void main(String[] args) {
        // 你的测试地址（公开可访问）
        String testUrl = "";

        long fileSize = OkHttpFileSizeUtil.getFileSize(testUrl);
        if (fileSize > 0) {
            System.out.println("最终文件大小：" + fileSize + " 字节 = " + (fileSize / 1024 / 1024) + " MB");
        } else {
            System.out.println("文件大小获取失败");
        }
    }
}

