package com.example.springboot.mapper;
//UserMapper的作用：将数据库中的数据映射到实体类中

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UserMapper {

    List<User> list();

    List<User> listByCondition(BaseRequest baseRequest);

    void save(User user);

    User getById(Integer id);

    void update(User user);

    void deleteById(Integer id);


}
