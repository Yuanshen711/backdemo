package com.orangesoftware.back.controller;

import com.orangesoftware.back.DTO.LoginDTO;
import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.entity.User;
import com.orangesoftware.back.service.JwtUtil;
import com.orangesoftware.back.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Resource
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        // 验证邮箱格式（如果提供了邮箱）
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            if (!isValidEmail(user.getEmail())) {
                return Result.error("邮箱格式不正确");
            }
        }
        
        return userService.register(user);
    }
    
    // 邮箱格式验证辅助方法
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @GetMapping("/profile")
    public Result getUserProfile(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error("未认证用户");
        }
        
        User user = userService.findUserByUsername(username);
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("userId", user.getUserId());
            return Result.success(userData);
        }
        
        return Result.error("用户信息获取失败");
    }

    }

