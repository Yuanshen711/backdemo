package com.orangesoftware.back.service;

import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.entity.Submit;
import com.orangesoftware.back.entity.SubmitVO;

import java.util.List;

public interface SubmitService {

    /**
     * 保存提交内容到数据库
     */
    void saveSubmit(Submit submit);

    /**
     * 查询所有提交记录
     */
    Result<List<SubmitVO>> getAllSubmits();

    /**
     * 根据用户ID查询提交记录
     */
    Result<List<SubmitVO>> getSubmitsByUserId(Integer userId);

    /**
     * 根据任务ID查询提交记录
     */
    Result<List<SubmitVO>> getSubmitsByTaskId(Integer taskId);

    /**
     * 根据ID查询提交记录
     */
    Result<SubmitVO> getSubmitById(Integer id);

    /**
     * 更新提交状态
     */
    Result<String> updateSubmitStatus(Integer id, Integer status);
}
