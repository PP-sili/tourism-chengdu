package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.tourismchengdu.entity.TicketStock;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TicketStockMapper {

    @Select("SELECT id, scene_id, stock_date, total_limit, create_time, update_time " +
            "FROM ticket_stock " +
            "WHERE scene_id = #{sceneId} AND stock_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY stock_date ASC")
    List<TicketStock> findBySceneIdAndDateRange(@Param("sceneId") Long sceneId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    @Select("SELECT id, scene_id, stock_date, total_limit, create_time, update_time " +
            "FROM ticket_stock WHERE scene_id = #{sceneId} AND stock_date = #{stockDate}")
    TicketStock findBySceneIdAndDate(@Param("sceneId") Long sceneId, @Param("stockDate") LocalDate stockDate);
}
