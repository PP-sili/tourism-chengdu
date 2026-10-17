package org.example.tourismchengdu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.tourismchengdu.entity.AppUser;

@Mapper
public interface AppUserMapper {

    @Select("SELECT id, phone, nickname, avatar, password, status, last_login_time, create_time, update_time " +
            "FROM app_user WHERE phone = #{phone}")
    AppUser findByPhone(String phone);

    @Select("SELECT id, phone, nickname, avatar, password, status, last_login_time, create_time, update_time " +
            "FROM app_user WHERE id = #{id}")
    AppUser findById(Long id);

    @Insert("INSERT INTO app_user (phone, nickname, avatar, password, status) " +
            "VALUES (#{phone}, #{nickname}, #{avatar}, #{password}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AppUser user);

    @Update("UPDATE app_user SET last_login_time = NOW() WHERE id = #{id}")
    int updateLastLoginTime(Long id);

    @Update("UPDATE app_user SET nickname = #{nickname}, avatar = #{avatar} WHERE id = #{id}")
    int updateProfile(AppUser user);
}
