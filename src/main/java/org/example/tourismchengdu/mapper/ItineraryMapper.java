package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.Itinerary;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ItineraryMapper {

    @Select("SELECT id, user_id, visit_date, scene_order, note, create_time, update_time " +
            "FROM itinerary WHERE user_id = #{userId} AND visit_date >= #{fromDate} ORDER BY visit_date ASC")
    List<Itinerary> findByUserIdFromDate(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);

    @Select("SELECT id, user_id, visit_date, scene_order, note, create_time, update_time " +
            "FROM itinerary WHERE user_id = #{userId} AND visit_date = #{visitDate}")
    Itinerary findByUserIdAndDate(@Param("userId") Long userId, @Param("visitDate") LocalDate visitDate);

    @Insert("INSERT INTO itinerary (user_id, visit_date, scene_order, note) " +
            "VALUES (#{userId}, #{visitDate}, #{sceneOrder}, #{note})")
    int insert(Itinerary itinerary);

    @Update("UPDATE itinerary SET scene_order = #{sceneOrder}, note = #{note} WHERE user_id = #{userId} AND visit_date = #{visitDate}")
    int update(Itinerary itinerary);
}
