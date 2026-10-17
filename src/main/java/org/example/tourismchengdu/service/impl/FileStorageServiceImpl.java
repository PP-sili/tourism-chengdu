package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.config.UploadProperties;
import org.example.tourismchengdu.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final DateTimeFormatter DATE_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");

    private final UploadProperties uploadProperties;
    private final Path basePath;

    public FileStorageServiceImpl(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
        this.basePath = Paths.get(uploadProperties.getBaseDir()).toAbsolutePath().normalize();
    }

    @Override
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "上传文件不能为空");
        }

        validateFile(file);

        String extension = resolveExtension(file);
        String dateDir = LocalDate.now().format(DATE_DIR_FORMATTER);
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;

        Path targetDir = basePath.resolve(dateDir);
        Path targetFile = targetDir.resolve(filename);

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile);
        } catch (IOException ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "文件保存失败");
        }

        String urlPrefix = uploadProperties.getUrlPrefix();
        if (!urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix + "/";
        }
        return urlPrefix + dateDir + "/" + filename;
    }

    public Path getBasePath() {
        return basePath;
    }

    private void validateFile(MultipartFile file) {
        long maxBytes = uploadProperties.getMaxSizeMb() * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "图片大小不能超过 " + uploadProperties.getMaxSizeMb() + "MB");
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !uploadProperties.getAllowedContentTypes().contains(contentType)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "仅支持 JPG、PNG、WEBP、GIF 图片");
        }
    }

    private String resolveExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            if (ext.matches("\\.(jpg|jpeg|png|webp|gif)")) {
                return ext;
            }
        }

        return switch (file.getContentType()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
