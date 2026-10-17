package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.tourismchengdu.entity.StatDaily;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StatDailyMapper {

    @Select("SELECT id, stat_date, scene_id, scene_name, visitor_count, ticket_sales, sales_amount, " +
            "refund_count, refund_amount, create_time, update_time " +
            "FROM stat_daily " +
            "WHERE scene_id = #{sceneId} AND stat_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY stat_date ASC")
    List<StatDaily> findBySceneIdAndDateRange(@Param("sceneId") Long sceneId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}
