package com.orangesoftware.back.entity;

import lombok.Data;

@Data
public class SubmitVO {
    private Integer id;
    private Integer userId;
    private Integer taskId;
    private String content;
    private String username;
    private String taskName;
    private String submitTime;
    private Integer status;
}
