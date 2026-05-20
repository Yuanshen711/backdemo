package com.orangesoftware.back.service;

import com.orangesoftware.back.entity.Result;

public interface TaskService {
    
    Result getAllTasks();
    
    Result getTasksByUserId(Integer userId);
    
    Result getTasksByStatus(Integer status);
}