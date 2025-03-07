package com.example.springboot.service;
//IUserService的作用：定义用户相关的业务逻辑
import com.example.springboot.controller.request.UserPageRequest;
import com.example.springboot.entity.User;

import java.util.List;

public interface IUserService {
    List<User> list();
    Object page(UserPageRequest userPageRequest);

    void save(User user);

    User getById(Integer id);

    void update(User user);

    void deleteById(Integer id);
}
