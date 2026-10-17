package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.dto.banner.BannerVO;
import org.example.tourismchengdu.mapper.BannerMapper;
import org.example.tourismchengdu.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    public BannerServiceImpl(BannerMapper bannerMapper) {
        this.bannerMapper = bannerMapper;
    }

    @Override
    public List<BannerVO> listVisibleBanners() {
        return bannerMapper.findAllVisible().stream()
                .map(BannerVO::from)
                .toList();
    }
}
