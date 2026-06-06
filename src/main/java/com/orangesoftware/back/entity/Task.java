package com.orangesoftware.back.entity;

import lombok.Data;

@Data
public class Task {
    private Integer taskId;
    private String taskName;
    private Integer status;
    private Integer userId;
    private String description;
    private Integer submitStatus;
}