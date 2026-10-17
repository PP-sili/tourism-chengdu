package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.Product;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT id, name, description, price, original_price, stock, sales_count, cover_image, images, " +
            "category, status, sort_order, create_time, update_time FROM product WHERE status = 1 " +
            "ORDER BY sort_order ASC, id ASC LIMIT #{offset}, #{limit}")
    List<Product> findVisible(@Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT id, name, description, price, original_price, stock, sales_count, cover_image, images, " +
            "category, status, sort_order, create_time, update_time FROM product WHERE status = 1 " +
            "<if test='category != null and category != \"\"'>AND category = #{category}</if> " +
            "ORDER BY sort_order ASC, id ASC LIMIT #{offset}, #{limit}</script>")
    List<Product> findVisibleByCategory(@Param("category") String category,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM product WHERE status = 1")
    long countVisible();

    @Select("<script>SELECT COUNT(1) FROM product WHERE status = 1 " +
            "<if test='category != null and category != \"\"'>AND category = #{category}</if></script>")
    long countVisibleByCategory(@Param("category") String category);

    @Select("SELECT id, name, description, price, original_price, stock, sales_count, cover_image, images, " +
            "category, status, sort_order, create_time, update_time FROM product WHERE id = #{id} AND status = 1")
    Product findById(Long id);

    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") int quantity);

    @Update("UPDATE product SET sales_count = sales_count + #{quantity} WHERE id = #{id}")
    int incrementSalesCount(@Param("id") Long id, @Param("quantity") int quantity);
}
