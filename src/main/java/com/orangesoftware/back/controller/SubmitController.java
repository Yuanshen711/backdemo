package com.orangesoftware.back.controller;

import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.entity.SubmitVO;
import com.orangesoftware.back.service.SubmitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubmitController {

    @Resource
    private SubmitService submitService;

    @GetMapping("/submits")
    public Result<List<SubmitVO>> getSubmitList(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer taskId) {
        if (userId != null) {
            return submitService.getSubmitsByUserId(userId);
        } else if (taskId != null) {
            return submitService.getSubmitsByTaskId(taskId);
        } else {
            return submitService.getAllSubmits();
        }
    }

    @GetMapping("/submit/{id}")
    public Result<SubmitVO> getSubmitDetail(@PathVariable Integer id) {
        return submitService.getSubmitById(id);
    }

    @PutMapping("/submit/{id}/status")
    public Result<String> updateStatus(@PathVariable Integer id,
                                       @RequestParam Integer status) {
        return submitService.updateSubmitStatus(id, status);
    }
}
