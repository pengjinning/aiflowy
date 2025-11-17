package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import tech.aiflowy.common.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class FilePreviewController {

    // 定义图片存储的基础路径
    @Value("${aiflowy.storage.local.root}")
    private  String fileUploadPath;

    @SaIgnore
    @GetMapping("/api/images/**")
    public ResponseEntity<byte[]> getImage(HttpServletRequest request) throws IOException {
        // 获取完整的路径
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String basePath = "/api/images/";
        String filePath = fullPath.substring(basePath.length()-1);
        String imagePath = getRootPath() + filePath;
        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 读取文件内容
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());

        // 返回文件内容
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, getContentType(imageFile))
                .body(fileContent);
    }

    // 根据文件扩展名获取 MIME 类型
    private String getContentType(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream"; // 默认类型
    }

    public String getRootPath() {
        if (StringUtil.hasText(this.fileUploadPath)) {
            return this.fileUploadPath;
        }
        ClassPathResource fileResource = new ClassPathResource("/");
        try {
            return new File(fileResource.getFile(), "/public").getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}