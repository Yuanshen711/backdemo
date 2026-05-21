package com.orangesoftware.back.service;

import com.orangesoftware.back.entity.Result;

public interface TaskService {
    
    Result<Object> getAllTasks();
    
    Result<Object> getTasksByUserId(Integer userId);
    
    Result<Object> getTasksByStatus(Integer status);

    Result<String> getTaskDetailById(Integer taskId);
}