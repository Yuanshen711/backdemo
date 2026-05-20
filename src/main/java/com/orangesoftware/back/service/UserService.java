package com.orangesoftware.back.service;

import com.orangesoftware.back.DTO.LoginDTO;
import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.entity.User;

public interface UserService {
    Result login(LoginDTO loginDTO);
    
    Result register(User user);
    
    User findUserByUsername(String username);
}
