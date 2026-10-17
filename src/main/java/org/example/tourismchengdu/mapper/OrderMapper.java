package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.Order;
import org.example.tourismchengdu.entity.OrderStatusCount;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO `order` (order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, refund_deadline, contact_name, contact_phone) " +
            "VALUES (#{orderNo}, #{userId}, #{orderType}, #{sceneId}, #{sceneName}, #{visitDate}, #{visitTimeSlot}, " +
            "#{ticketQuantity}, #{totalAmount}, #{actualAmount}, #{status}, #{refundDeadline}, #{contactName}, #{contactPhone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` WHERE order_no = #{orderNo}")
    Order findByOrderNo(String orderNo);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` WHERE order_no = #{orderNo} AND user_id = #{userId} AND order_type = 1")
    Order findTicketOrderByOrderNoAndUserId(@Param("orderNo") String orderNo, @Param("userId") Long userId);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` " +
            "WHERE user_id = #{userId} AND order_type = 1 AND status = #{status} " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Order> findTicketOrdersByUserIdAndStatus(@Param("userId") Long userId,
                                                  @Param("status") String status,
                                                  @Param("offset") int offset,
                                                  @Param("limit") int limit);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` " +
            "WHERE user_id = #{userId} AND order_type = 1 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Order> findTicketOrdersByUserId(@Param("userId") Long userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM `order` WHERE user_id = #{userId} AND order_type = 1 AND status = #{status}")
    long countTicketOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Select("SELECT COUNT(1) FROM `order` WHERE user_id = #{userId} AND order_type = 1")
    long countTicketOrdersByUserId(@Param("userId") Long userId);

    @Update("UPDATE `order` SET status = #{status}, pay_time = NOW() WHERE id = #{id} AND status = 'pending_pay'")
    int markPaid(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE `order` SET status = 'refunded', refund_time = NOW() WHERE id = #{id} AND status = 'pending_use'")
    int markRefunded(Long id);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` " +
            "WHERE user_id = #{userId} AND order_type = 1 AND status = 'pending_use' AND visit_date >= #{fromDate} " +
            "ORDER BY visit_date ASC, visit_time_slot ASC, id ASC")
    List<Order> findPendingUseTicketOrdersFromDate(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` " +
            "WHERE user_id = #{userId} AND order_type = 1 AND status = 'pending_use' AND visit_date = #{visitDate} " +
            "ORDER BY visit_time_slot ASC, id ASC")
    List<Order> findPendingUseTicketOrdersByUserAndDate(@Param("userId") Long userId, @Param("visitDate") LocalDate visitDate);

    @Insert("INSERT INTO `order` (order_no, user_id, order_type, total_amount, actual_amount, status, " +
            "receiver_name, receiver_phone, receiver_address, remark) " +
            "VALUES (#{orderNo}, #{userId}, #{orderType}, #{totalAmount}, #{actualAmount}, #{status}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverAddress}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMallOrder(Order order);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` WHERE order_no = #{orderNo} AND user_id = #{userId} AND order_type = 2")
    Order findMallOrderByOrderNoAndUserId(@Param("orderNo") String orderNo, @Param("userId") Long userId);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` " +
            "WHERE user_id = #{userId} AND order_type = 2 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Order> findMallOrdersByUserId(@Param("userId") Long userId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    @Select("SELECT id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, " +
            "ticket_quantity, total_amount, actual_amount, status, pay_time, refund_time, refund_deadline, " +
            "contact_name, contact_phone, receiver_name, receiver_phone, receiver_address, logistics_no, remark, " +
            "create_time, update_time FROM `order` " +
            "WHERE user_id = #{userId} AND order_type = 2 AND status = #{status} " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Order> findMallOrdersByUserIdAndStatus(@Param("userId") Long userId,
                                                @Param("status") String status,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM `order` WHERE user_id = #{userId} AND order_type = 2")
    long countMallOrdersByUserId(Long userId);

    @Select("SELECT COUNT(1) FROM `order` WHERE user_id = #{userId} AND order_type = 2 AND status = #{status}")
    long countMallOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Update("UPDATE `order` SET status = 'pending_ship', pay_time = NOW() WHERE id = #{id} AND status = 'pending_pay'")
    int markMallPaid(Long id);

    @Select("SELECT status, COUNT(1) AS count FROM `order` WHERE user_id = #{userId} AND order_type = #{orderType} GROUP BY status")
    List<OrderStatusCount> countByUserIdGroupByStatus(@Param("userId") Long userId, @Param("orderType") int orderType);

    @Select("SELECT COUNT(1) > 0 FROM `order` WHERE user_id = #{userId} AND scene_id = #{sceneId} AND order_type = 1 " +
            "AND status IN ('pending_use', 'used') AND visit_date <= CURDATE()")
    boolean hasVisitedScene(@Param("userId") Long userId, @Param("sceneId") Long sceneId);
}
