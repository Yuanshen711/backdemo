package com.orangesoftware.back.service.imply;

import com.orangesoftware.back.DTO.LoginDTO;
import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.entity.User;
import com.orangesoftware.back.mapper.UserMapper;
import com.orangesoftware.back.utill.JwtUtil;
import com.orangesoftware.back.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImply implements UserService {
    
    @Resource
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public Result login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        if (username == null || "".equals(username.trim())){
            return Result.error("账号不能为空");
        }
        if (password == null || "".equals(password.trim())){
            return Result.error("密码不能为空");
        }
        User user = userMapper.findByUsername(username);
        if(user == null) {
            return Result.error("用户不存在");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(password, user.getPassword())){
            return Result.error("密码错误");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(username);
        
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("userId", user.getUserId());
        map.put("token", token);
        
        return Result.success(map);
    }
    
    @Override
    public Result register(User user) {
        // 输入验证
        if (user == null) {
            return Result.error("用户信息不能为空");
        }
        
        if (user.getUsername() == null || user.getPassword() == null || 
            user.getUsername().trim().isEmpty() || user.getPassword().trim().isEmpty()) {
            return Result.error("用户名和密码不能为空");
        }
        
        // 验证用户名长度
        if (user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            return Result.error("用户名长度必须在3-20个字符之间");
        }
        
        // 验证密码强度
        if (user.getPassword().length() < 6 || user.getPassword().length() > 30) {
            return Result.error("密码长度必须在6-30个字符之间");
        }
        
        // 验证密码复杂度
        if (!isPasswordStrong(user.getPassword())) {
            return Result.error("密码必须包含字母和数字");
        }
        
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }
        
        // 对密码进行加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        try {
            userMapper.insertUser(user);
            // 注册成功后自动登录，返回token
            String token = jwtUtil.generateToken(user.getUsername());
            Map<String, Object> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("userId", user.getUserId());
            map.put("token", token);
            return Result.success(map, "注册成功");
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }
    
    @Override
    public User findUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    // 密码强度验证辅助方法
    private boolean isPasswordStrong(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        
        return hasLetter && hasDigit;
    }
}
