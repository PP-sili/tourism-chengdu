package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.example.tourismchengdu.entity.OrderItem;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Insert("INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{productPrice}, #{quantity})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem item);

    @Select("SELECT id, order_id, product_id, product_name, product_price, quantity, create_time " +
            "FROM order_item WHERE order_id = #{orderId} ORDER BY id ASC")
    List<OrderItem> findByOrderId(Long orderId);
}
