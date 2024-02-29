package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

public interface UserService {

    /**
     * 用户登录
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
