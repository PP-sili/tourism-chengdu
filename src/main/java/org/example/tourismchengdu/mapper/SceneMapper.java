package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.tourismchengdu.entity.Scene;

import java.util.List;

@Mapper
public interface SceneMapper {

    @Select("SELECT id, name, subtitle, cover_image, images, history_text, address, city, " +
            "ticket_price, student_price, child_price, open_time, suggested_duration, contact_phone, " +
            "daily_limit, status, sort_order, create_time, update_time " +
            "FROM scene WHERE status = 1 ORDER BY sort_order ASC, id ASC")
    List<Scene> findAllVisible();

    @Select("SELECT id, name, subtitle, cover_image, images, history_text, address, city, " +
            "ticket_price, student_price, child_price, open_time, suggested_duration, contact_phone, " +
            "daily_limit, status, sort_order, create_time, update_time " +
            "FROM scene WHERE id = #{id} AND status = 1")
    Scene findById(Long id);
}
