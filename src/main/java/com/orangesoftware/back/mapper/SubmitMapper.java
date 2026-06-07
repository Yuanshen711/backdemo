package com.orangesoftware.back.mapper;

import com.orangesoftware.back.entity.Submit;
import com.orangesoftware.back.entity.SubmitVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SubmitMapper {

    @Insert("INSERT INTO submit_table (user_id, task_id, content,submit_time,status) VALUES (#{userId}, #{taskId}, #{content},NOW(),#{status})")
    int insert(Submit submit);

    @Results(id = "submitVOMap", value = {
        @Result(property = "userId", column = "user_id"),
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "submitTime",column = "submit_time"),
        @Result(property = "status",column = "status"),
        @Result(property = "username", column = "username")
    })
    @Select("SELECT s.*, u.username, t.task_name FROM submit_table s " +
            "LEFT JOIN user u ON s.user_id = u.user_id " +
            "LEFT JOIN task t ON s.task_id = t.task_id")
    List<SubmitVO> findAll();

    @ResultMap("submitVOMap")
    @Select("SELECT s.*, u.username, t.task_name FROM submit_table s " +
            "LEFT JOIN user u ON s.user_id = u.user_id " +
            "LEFT JOIN task t ON s.task_id = t.task_id " +
            "WHERE s.user_id = #{userId}")
    List<SubmitVO> findByUserId(Integer userId);

    @ResultMap("submitVOMap")
    @Select("SELECT s.*, u.username, t.task_name FROM submit_table s " +
            "LEFT JOIN user u ON s.user_id = u.user_id " +
            "LEFT JOIN task t ON s.task_id = t.task_id " +
            "WHERE s.task_id = #{taskId}")
    List<SubmitVO> findByTaskId(Integer taskId);

    @ResultMap("submitVOMap")
    @Select("SELECT s.*, u.username, t.task_name FROM submit_table s " +
            "LEFT JOIN user u ON s.user_id = u.user_id " +
            "LEFT JOIN task t ON s.task_id = t.task_id " +
            "WHERE s.id = #{id}")
    SubmitVO findById(Integer id);

    @Update("UPDATE submit_table SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}
