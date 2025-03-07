package com.example.springboot.mapper;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.PasswordRequest;

import com.example.springboot.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {

    List<Admin> list();

    List<Admin> listByCondition(BaseRequest baseRequest);

    void save(Admin obj);

    Admin getById(Integer id);

    void update(Admin obj);

    void deleteById(Integer id);

    Admin getByUsernameAndPassword(LoginRequest request);

    Admin getByAdminname(String username);

    int updatePassword(PasswordRequest request);

    int checkPassword(PasswordRequest request);

    void signup(Admin obj);

    Admin getByUsername(String username);
}
