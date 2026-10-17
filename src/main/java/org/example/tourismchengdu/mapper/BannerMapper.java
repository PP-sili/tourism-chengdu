package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.tourismchengdu.entity.Banner;

import java.util.List;

@Mapper
public interface BannerMapper {

    @Select("SELECT id, scene_id, image_url, title, sub_title, sort_order, status, create_time, update_time " +
            "FROM banner WHERE status = 1 ORDER BY sort_order ASC, id ASC")
    List<Banner> findAllVisible();
}
