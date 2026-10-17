package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.SceneEvaluation;

import java.util.List;

@Mapper
public interface SceneEvaluationMapper {

    @Insert("INSERT INTO scene_evaluation (user_id, scene_id, eval_type, content, rating, status) " +
            "VALUES (#{userId}, #{sceneId}, #{evalType}, #{content}, #{rating}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SceneEvaluation evaluation);

    @Update("UPDATE scene_evaluation SET eval_type = #{evalType}, content = #{content}, rating = #{rating} " +
            "WHERE id = #{id} AND user_id = #{userId} AND status = 1")
    int updateByIdAndUserId(SceneEvaluation evaluation);

    @Select("SELECT id, user_id, scene_id, eval_type, content, rating, status, create_time, update_time " +
            "FROM scene_evaluation WHERE id = #{id} AND status = 1")
    SceneEvaluation findVisibleById(Long id);

    @Select("SELECT id, user_id, scene_id, eval_type, content, rating, status, create_time, update_time " +
            "FROM scene_evaluation WHERE user_id = #{userId} AND scene_id = #{sceneId} AND status = 1")
    SceneEvaluation findByUserIdAndSceneId(@Param("userId") Long userId, @Param("sceneId") Long sceneId);

    @Select("SELECT id, user_id, scene_id, eval_type, content, rating, status, create_time, update_time " +
            "FROM scene_evaluation WHERE scene_id = #{sceneId} AND status = 1 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<SceneEvaluation> findBySceneId(@Param("sceneId") Long sceneId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select("SELECT id, user_id, scene_id, eval_type, content, rating, status, create_time, update_time " +
            "FROM scene_evaluation WHERE scene_id = #{sceneId} AND eval_type = #{evalType} AND status = 1 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<SceneEvaluation> findBySceneIdAndType(@Param("sceneId") Long sceneId,
                                               @Param("evalType") String evalType,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM scene_evaluation WHERE scene_id = #{sceneId} AND status = 1")
    long countBySceneId(Long sceneId);

    @Select("SELECT COUNT(1) FROM scene_evaluation WHERE scene_id = #{sceneId} AND eval_type = #{evalType} AND status = 1")
    long countBySceneIdAndType(@Param("sceneId") Long sceneId, @Param("evalType") String evalType);

    @Select("SELECT id, user_id, scene_id, eval_type, content, rating, status, create_time, update_time " +
            "FROM scene_evaluation WHERE user_id = #{userId} AND status = 1 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<SceneEvaluation> findByUserId(@Param("userId") Long userId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM scene_evaluation WHERE user_id = #{userId} AND status = 1")
    long countByUserId(Long userId);
}
