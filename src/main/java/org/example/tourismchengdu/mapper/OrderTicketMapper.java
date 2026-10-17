package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.OrderTicket;

import java.util.List;

@Mapper
public interface OrderTicketMapper {

    @Insert("INSERT INTO order_ticket (order_id, ticket_type, visitor_name, id_card_no, unit_price, qr_code, status) " +
            "VALUES (#{orderId}, #{ticketType}, #{visitorName}, #{idCardNo}, #{unitPrice}, #{qrCode}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderTicket ticket);

    @Select("SELECT id, order_id, ticket_type, visitor_name, id_card_no, unit_price, qr_code, status, create_time " +
            "FROM order_ticket WHERE order_id = #{orderId} ORDER BY id ASC")
    List<OrderTicket> findByOrderId(Long orderId);

    @Update("UPDATE order_ticket SET qr_code = #{qrCode}, status = #{status} WHERE id = #{id}")
    int updateQrCodeAndStatus(@Param("id") Long id, @Param("qrCode") String qrCode, @Param("status") String status);

    @Update("UPDATE order_ticket SET status = 'refunded' WHERE order_id = #{orderId}")
    int markRefundedByOrderId(Long orderId);
}
