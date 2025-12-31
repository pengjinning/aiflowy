package tech.aiflowy.common.util;

import okhttp3.*;
import okio.BufferedSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class OkHttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OkHttpUtil.class);
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");


    private static OkHttpClient getOkHttpClient() {
        return OkHttpClientUtil.buildDefaultClient();
    }


    public static String get(String url) {
        return executeString(url, "GET", null, null);
    }

    public static long getSize(String url) {

        return 0l;
    }

    public static byte[] getBytes(String url) {
        return executeBytes(url, "GET", null, null);
    }

    public static String get(String url, Map<String, String> headers) {
        return executeString(url, "GET", headers, null);
    }

    public static InputStream getInputStream(String url) {
        try (Response response = getOkHttpClient().newCall(new Request.Builder().url(url).build()).execute();
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.byteStream();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP getInputStream failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }

    public static String post(String url, Map<String, String> headers, String payload) {
        return executeString(url, "POST", headers, payload);
    }

    public static byte[] postBytes(String url, Map<String, String> headers, String payload) {
        return executeBytes(url, "POST", headers, payload);
    }

    public static String put(String url, Map<String, String> headers, String payload) {
        return executeString(url, "PUT", headers, payload);
    }

    public static String delete(String url, Map<String, String> headers, String payload) {
        return executeString(url, "DELETE", headers, payload);
    }

    public static String multipartString(String url, Map<String, String> headers, Map<String, Object> payload) {
        try (Response response = multipart(url, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.string();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP multipartString failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }

    public static byte[] multipartBytes(String url, Map<String, String> headers, Map<String, Object> payload) {
        try (Response response = multipart(url, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.bytes();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP multipartBytes failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }


    public static String executeString(String url, String method, Map<String, String> headers, Object payload) {
        try (Response response = execute0(url, method, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.string();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP executeString failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }

    public static byte[] executeBytes(String url, String method, Map<String, String> headers, Object payload) {
        try (Response response = execute0(url, method, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.bytes();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP executeBytes failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }

    private static Response execute0(String url, String method, Map<String, String> headers, Object payload) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        Request request;
        if ("GET".equalsIgnoreCase(method)) {
            request = builder.build();
        } else {
            RequestBody body = RequestBody.create(payload == null ? "" : payload.toString(), JSON_TYPE);
            request = builder.method(method, body).build();
        }

        return getOkHttpClient().newCall(request).execute();
    }

    public static Response multipart(String url, Map<String, String> headers, Map<String, Object> payload) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        MultipartBody.Builder mbBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        payload.forEach((key, value) -> {
            if (value instanceof File) {
                File file = (File) value;
                RequestBody body = RequestBody.create(file, MediaType.parse("application/octet-stream"));
                mbBuilder.addFormDataPart(key, file.getName(), body);
            } else if (value instanceof InputStream) {
                RequestBody body = new InputStreamRequestBody(MediaType.parse("application/octet-stream"), (InputStream) value);
                mbBuilder.addFormDataPart(key, key, body);
            } else if (value instanceof byte[]) {
                mbBuilder.addFormDataPart(key, key, RequestBody.create((byte[]) value));
            } else {
                mbBuilder.addFormDataPart(key, String.valueOf(value));
            }
        });

        MultipartBody multipartBody = mbBuilder.build();
        Request request = builder.post(multipartBody).build();

        return getOkHttpClient().newCall(request).execute();
    }


    public static class InputStreamRequestBody extends RequestBody {
        private final InputStream inputStream;
        private final MediaType contentType;

        public InputStreamRequestBody(MediaType contentType, InputStream inputStream) {
            if (inputStream == null) throw new NullPointerException("inputStream == null");
            this.contentType = contentType;
            this.inputStream = inputStream;
        }

        @Override
        public MediaType contentType() {
            return contentType;
        }

        @Override
        public long contentLength() throws IOException {
            return inputStream.available() == 0 ? -1 : inputStream.available();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            IOUtil.copy(inputStream, sink);
        }
    }
}
