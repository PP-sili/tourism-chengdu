package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.example.tourismchengdu.entity.RefundRecord;

@Mapper
public interface RefundRecordMapper {

    @Insert("INSERT INTO refund_record (order_id, user_id, scene_id, scene_name, visit_date, ticket_quantity, " +
            "refund_amount, reason) VALUES (#{orderId}, #{userId}, #{sceneId}, #{sceneName}, #{visitDate}, " +
            "#{ticketQuantity}, #{refundAmount}, #{reason})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RefundRecord record);
}
