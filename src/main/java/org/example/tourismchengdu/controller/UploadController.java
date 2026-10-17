package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.upload.UploadImageVO;
import org.example.tourismchengdu.service.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "上传", description = "图片上传（头像、帖子）")
public class UploadController {

    private final FileStorageService fileStorageService;

    public UploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/image")
    public Result<UploadImageVO> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeImage(file);
        UploadImageVO vo = new UploadImageVO(url, file.getOriginalFilename(), file.getSize());
        return Result.success(vo);
    }
}
