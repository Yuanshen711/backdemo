package com.orangesoftware.back.service.imply;

import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.mapper.TaskMapper;
import com.orangesoftware.back.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImply implements TaskService {

    @Resource
    private TaskMapper taskMapper;

    @Override
    public Result<Object> getAllTasks() {
        try {
            return Result.success(taskMapper.findAllTasks());
        } catch (Exception e) {
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Object> getTasksByUserId(Integer userId) {
        try {
            return Result.success(taskMapper.findTasksByUserId(userId));
        } catch (Exception e) {
            return Result.error("获取用户任务列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Object> getTasksByStatus(Integer status) {
        try {
            return Result.success(taskMapper.findTasksByStatus(status));
        } catch (Exception e) {
            return Result.error("获取状态任务列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> getTaskDetailById(Integer taskId) {
        try {
            String description = taskMapper.findTaskDetailById(taskId);
            return Result.success(description);
        } catch (Exception e) {
            return Result.error("获取任务详情失败: " + e.getMessage());
        }
    }

}
