package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.tourismchengdu.entity.PostLike;

@Mapper
public interface PostLikeMapper {

    @Insert("INSERT INTO post_like (post_id, user_id) VALUES (#{postId}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PostLike postLike);

    @Delete("DELETE FROM post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    int delete(@Param("postId") Long postId, @Param("userId") Long userId);

    @Select("SELECT COUNT(1) FROM post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    int exists(@Param("postId") Long postId, @Param("userId") Long userId);
}
