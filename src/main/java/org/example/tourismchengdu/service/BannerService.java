package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.banner.BannerVO;

import java.util.List;

public interface BannerService {

    List<BannerVO> listVisibleBanners();
}
