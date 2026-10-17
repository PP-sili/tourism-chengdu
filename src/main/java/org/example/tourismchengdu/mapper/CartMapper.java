package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.Cart;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("SELECT id, user_id, product_id, product_name, product_price, quantity, selected, create_time, update_time " +
            "FROM cart WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Cart> findByUserId(Long userId);

    @Select("SELECT id, user_id, product_id, product_name, product_price, quantity, selected, create_time, update_time " +
            "FROM cart WHERE user_id = #{userId} AND selected = 1 ORDER BY create_time DESC")
    List<Cart> findSelectedByUserId(Long userId);

    @Select("SELECT id, user_id, product_id, product_name, product_price, quantity, selected, create_time, update_time " +
            "FROM cart WHERE id = #{id} AND user_id = #{userId}")
    Cart findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT id, user_id, product_id, product_name, product_price, quantity, selected, create_time, update_time " +
            "FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Insert("INSERT INTO cart (user_id, product_id, product_name, product_price, quantity, selected) " +
            "VALUES (#{userId}, #{productId}, #{productName}, #{productPrice}, #{quantity}, #{selected})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);

    @Update("UPDATE cart SET product_name = #{productName}, product_price = #{productPrice}, " +
            "quantity = #{quantity}, selected = #{selected} WHERE id = #{id} AND user_id = #{userId}")
    int update(Cart cart);

    @Delete("DELETE FROM cart WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("<script>DELETE FROM cart WHERE user_id = #{userId} AND id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    int deleteByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    @Select("SELECT COUNT(1) FROM cart WHERE user_id = #{userId}")
    long countByUserId(Long userId);
}
