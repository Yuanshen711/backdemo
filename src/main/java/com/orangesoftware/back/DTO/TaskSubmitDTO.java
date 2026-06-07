package com.orangesoftware.back.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 任务提交请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    private Integer taskId;


    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 提交内容
     */
    private String content;
}
