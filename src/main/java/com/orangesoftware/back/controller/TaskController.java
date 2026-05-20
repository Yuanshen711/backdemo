package com.orangesoftware.back.controller;

import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Resource
    private TaskService taskService;

    @GetMapping("/tasks")
    public Result getTaskList(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer status) {
        if (userId != null) {
            return taskService.getTasksByUserId(userId);
        } else if (status != null) {
            return taskService.getTasksByStatus(status);
        } else {
            return taskService.getAllTasks();
        }
    }
}
