package tech.aiflowy.common.filestorage.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于 OkHttp 的网络文件流读取工具（高性能）
 */
public class OkHttpFileStreamReader {

    // 全局单例 OkHttpClient（复用连接池，提升性能）
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 连接超时
            .readTimeout(60, TimeUnit.SECONDS)   // 读取超时
            .writeTimeout(30, TimeUnit.SECONDS)  // 写入超时
            .retryOnConnectionFailure(true)      // 连接失败自动重试
            .build();

    /**
     * 读取网络文件流
     * @param fileUrl 网络文件地址
     * @param headers 自定义请求头（可选）
     * @param start 起始字节（断点续传）
     * @param end 结束字节
     * @return InputStream
     * @throws Exception
     */
    public static InputStream readHttpStream(String fileUrl, Map<String, String> headers, Long start, Long end) throws Exception {
        // 1. 构建请求
        Request.Builder requestBuilder = new Request.Builder().url(fileUrl);

        // 2. 添加自定义头
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(requestBuilder::addHeader);
        }

        // 3. 断点续传配置
        if (start != null) {
            String range = "bytes=" + start + "-" + (end != null ? end : "");
            requestBuilder.addHeader("Range", range);
        }

        // 4. 发送请求
        Response response = OK_HTTP_CLIENT.newCall(requestBuilder.build()).execute();

        // 5. 校验响应
        if (!response.isSuccessful()) {
            response.close();
            throw new Exception("请求失败，状态码：" + response.code() + "，地址：" + fileUrl);
        }

        // 6. 返回流（自动关闭响应）
        InputStream inputStream = response.body().byteStream();
        return new InputStream() {
            @Override
            public int read() {
                try {
                    return inputStream.read();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int read(byte[] b){
                try {
                    return inputStream.read(b);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int read(byte[] b, int off, int len){
                try {
                    return inputStream.read(b, off, len);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                response.close();
            }
        };
    }

    // ------------------------------ 简化调用 ------------------------------
    public static InputStream readHttpStream(String fileUrl) throws Exception {
        return readHttpStream(fileUrl, null, null, null);
    }

    // ------------------------------ 测试 ------------------------------
    public static void main(String[] args) {
        String testUrl = "https://xxx.oss.com/file.pdf";
        try (InputStream is = readHttpStream(testUrl)) {
            // 业务处理（如上传到 OSS、解析文件内容等）
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
