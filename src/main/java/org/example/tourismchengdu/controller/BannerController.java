package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.banner.BannerVO;
import org.example.tourismchengdu.service.BannerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@Tag(name = "首页", description = "轮播图")
@SecurityRequirements
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    public Result<List<BannerVO>> listBanners() {
        return Result.success(bannerService.listVisibleBanners());
    }
}
