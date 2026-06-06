package com.orangesoftware.back.mapper;

import com.orangesoftware.back.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper {
    
    @Results({
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "status", column = "status"),
        @Result(property = "userId", column = "user_id"),
            @Result(property = "submitStatus", column = "submit_status")
    })
    @Select("SELECT t.*, (SELECT status FROM submit_table WHERE task_id = t.task_id ORDER BY id DESC LIMIT 1) as submit_status FROM task t")
    List<Task> findAllTasks();
    
    @Results({
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "status", column = "status"),
        @Result(property = "userId", column = "user_id"),
            @Result(property = "submitStatus", column = "submit_status")})
    @Select("SELECT t.*, (SELECT status FROM submit_table WHERE task_id = t.task_id AND user_id = #{userId} " +
            "ORDER BY id DESC LIMIT 1) as submit_status FROM task t WHERE t.user_id = #{userId}")
    List<Task> findTasksByUserId(Integer userId);
    
    @Results({
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "status", column = "status"),
        @Result(property = "userId", column = "user_id")
    })
    @Select("SELECT * FROM task WHERE status = #{status}")
    List<Task> findTasksByStatus(Integer status);


    @Select("SELECT description FROM task WHERE task_id = #{taskId}")
    String findTaskDetailById(Integer taskId);
}