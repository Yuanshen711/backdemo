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
        @Result(property = "userId", column = "user_id")
    })
    @Select("SELECT * FROM task")
    List<Task> findAllTasks();
    
    @Results({
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "status", column = "status"),
        @Result(property = "userId", column = "user_id")
    })
    @Select("SELECT * FROM task WHERE user_id = #{userId}")
    List<Task> findTasksByUserId(Integer userId);
    
    @Results({
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "status", column = "status"),
        @Result(property = "userId", column = "user_id")
    })
    @Select("SELECT * FROM task WHERE status = #{status}")
    List<Task> findTasksByStatus(Integer status);
}