package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.Post;

import java.util.List;

@Mapper
public interface PostMapper {

    @Insert("INSERT INTO post (user_id, scene_id, scene_name, title, content, images, location, status) " +
            "VALUES (#{userId}, #{sceneId}, #{sceneName}, #{title}, #{content}, #{images}, #{location}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Post post);

    @Select("SELECT id, user_id, scene_id, scene_name, title, content, images, location, view_count, like_count, " +
            "status, create_time, update_time FROM post WHERE id = #{id} AND status = 1")
    Post findVisibleById(Long id);

    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    @Update("UPDATE post SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Update("UPDATE post SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
    int decrementLikeCount(Long id);

    @Select("SELECT id, user_id, scene_id, scene_name, title, content, images, location, view_count, like_count, " +
            "status, create_time, update_time FROM post WHERE status = 1 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Post> findByLatest(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT id, user_id, scene_id, scene_name, title, content, images, location, view_count, like_count, " +
            "status, create_time, update_time FROM post WHERE status = 1 " +
            "ORDER BY (like_count * 2 + view_count) DESC, create_time DESC LIMIT #{offset}, #{limit}")
    List<Post> findByHot(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM post WHERE status = 1")
    long countVisible();

    @Select("SELECT id, user_id, scene_id, scene_name, title, content, images, location, view_count, like_count, " +
            "status, create_time, update_time FROM post WHERE user_id = #{userId} AND status = 1 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Post> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM post WHERE user_id = #{userId} AND status = 1")
    long countByUserId(Long userId);

    @Select("SELECT COALESCE(SUM(like_count), 0) FROM post WHERE user_id = #{userId} AND status = 1")
    long sumLikeCountByUserId(Long userId);
}
