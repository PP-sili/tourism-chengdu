package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.TicketStockSlot;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TicketStockSlotMapper {

    @Select("SELECT id, scene_id, stock_date, slot_code, remaining, create_time, update_time " +
            "FROM ticket_stock_slot " +
            "WHERE scene_id = #{sceneId} AND stock_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY stock_date ASC, slot_code ASC")
    List<TicketStockSlot> findBySceneIdAndDateRange(@Param("sceneId") Long sceneId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    @Select("SELECT id, scene_id, stock_date, slot_code, remaining, create_time, update_time " +
            "FROM ticket_stock_slot " +
            "WHERE scene_id = #{sceneId} AND stock_date = #{stockDate} " +
            "ORDER BY slot_code ASC")
    List<TicketStockSlot> findBySceneIdAndDate(@Param("sceneId") Long sceneId,
                                               @Param("stockDate") LocalDate stockDate);

    @Select("SELECT id, scene_id, stock_date, slot_code, remaining, create_time, update_time " +
            "FROM ticket_stock_slot " +
            "WHERE scene_id = #{sceneId} AND stock_date = #{stockDate} AND slot_code = #{slotCode}")
    TicketStockSlot findBySceneIdAndDateAndSlot(@Param("sceneId") Long sceneId,
                                                @Param("stockDate") LocalDate stockDate,
                                                @Param("slotCode") String slotCode);

    @Select("SELECT COUNT(1) FROM ticket_stock_slot " +
            "WHERE scene_id = #{sceneId} AND stock_date = #{stockDate}")
    int countBySceneIdAndDate(@Param("sceneId") Long sceneId, @Param("stockDate") LocalDate stockDate);

    @Update("UPDATE ticket_stock_slot SET remaining = remaining - #{quantity} " +
            "WHERE scene_id = #{sceneId} AND stock_date = #{stockDate} AND slot_code = #{slotCode} " +
            "AND remaining >= #{quantity}")
    int deductStock(@Param("sceneId") Long sceneId,
                    @Param("stockDate") LocalDate stockDate,
                    @Param("slotCode") String slotCode,
                    @Param("quantity") int quantity);

    @Update("UPDATE ticket_stock_slot SET remaining = remaining + #{quantity} " +
            "WHERE scene_id = #{sceneId} AND stock_date = #{stockDate} AND slot_code = #{slotCode}")
    int restoreStock(@Param("sceneId") Long sceneId,
                     @Param("stockDate") LocalDate stockDate,
                     @Param("slotCode") String slotCode,
                     @Param("quantity") int quantity);
}
