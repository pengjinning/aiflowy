package tech.aiflowy.ai.node;

import cn.hutool.core.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.common.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public class InputStreamFile implements MultipartFile {

    private final InputStream inputStream;
    private final Map<String, String> headers;
    private String name;
    private byte[] bytes;

    public InputStreamFile(InputStream inputStream, Map<String, String> headers) {
        this.inputStream = inputStream;
        this.headers = headers;

        String contentDisposition = headers.get("Content-Disposition");
        if (StringUtil.hasText(contentDisposition)) {
            this.name = contentDisposition.split(";")[1].split("=")[1].replace("\"", "");
        }

        String contentType = headers.get("Content-Type");
        if (StringUtil.hasText(contentType)) {
            String suffix = contentType.split("/")[1];
            this.name = UUID.randomUUID() + "." + suffix;
        }
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public String getOriginalFilename() {
        return this.name;
    }

    public String getContentType() {
        return headers.get("Content-Type");
    }

    @Override
    public boolean isEmpty() {
        return inputStream != null;
    }

    @Override
    public long getSize() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public byte[] getBytes() throws IOException {
        if (this.bytes == null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            this.bytes = bos.toByteArray();
        }
        return this.bytes;
    }

    @NotNull
    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public void transferTo(@NotNull File dest) throws IOException, IllegalStateException {
        FileUtil.writeBytes(this.getBytes(), dest);
    }
}
