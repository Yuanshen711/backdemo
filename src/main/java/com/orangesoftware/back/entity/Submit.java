package com.orangesoftware.back.entity;

import lombok.Data;

@Data
public class Submit {
    private Integer id;
    private Integer userId;
    private Integer taskId;
    private String content;
    private Integer status;
}
