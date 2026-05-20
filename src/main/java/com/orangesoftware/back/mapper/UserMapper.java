package com.orangesoftware.back.mapper;

import com.orangesoftware.back.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    @Insert("INSERT INTO user (username, password, email) VALUES (#{username}, #{password}, #{email})")
    void insertUser(User user);
    
    @Select("SELECT * FROM user WHERE id = #{userId}")
    User findById(Long userId);
}
