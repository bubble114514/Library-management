package com.example.springboot.service.impl;
//Service的作用：聚合数据访问层对象，对数据进行统一处理，简化业务逻辑层对象和数据访问层的交互。

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.PasswordRequest;
import com.example.springboot.entity.Admin;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.AdminMapper;
import com.example.springboot.service.IAdminService;
import com.example.springboot.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AdminService implements IAdminService {
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    AdminMapper adminMapper;

    private static final String DEFAULT_PASSWORD = "123";
    private static final String PASS_SALT = "123";

    @Override
    public List<Admin> list() {
        return adminMapper.list();
    }

    @Override
    public PageInfo<Admin> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
        List<Admin> admins = adminMapper.listByCondition(baseRequest);
        return new PageInfo<>(admins);
    }

    @Override
    public void save(Admin obj) {

        //默认密码123
        if (StrUtil.isBlank(obj.getPassword())){
            obj.setPassword(DEFAULT_PASSWORD);
        }
        obj.setPassword(securePass(obj.getPassword()));//设置md5加密，加盐
        // 检查用户名是否已存在
        if (isUserNameExists(obj.getUsername())) {
            throw new ServiceException("用户名已存在！");
        }else{
            adminMapper.save(obj);
        }
    }

    @Override
    public Admin getById(Integer id) {
        return adminMapper.getById(id);
    }

    @Override
    public void update(Admin obj) {
        // 检查用户名是否已存在（排除自身）
        if (isUserNameExistsExcludingSelf(obj.getUsername(), obj.getId())) {
            throw new ServiceException("用户名已存在！");
        }
        adminMapper.update(obj);
    }

    @Override
    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    @Override
    public LoginDTO login(LoginRequest request) {

        Admin admin=null;
        try {
            admin = adminMapper.getByUsername(request.getUsername());
        } catch (Exception e) {
            log.error("根据用户名{}查询出错",request.getUsername());
            throw new ServiceException("用户名错误");
        }

        //判断密码是否合法
        String securePass =securePass(request.getPassword());
        if (!securePass.equals(admin.getPassword())){
            throw new ServiceException("用户名或密码错误");
        }
        if (!admin.isStatus()) {
            throw new ServiceException("当前用户禁用，请联系管理员");
        }
        LoginDTO loginDTO = new LoginDTO();
        BeanUtils.copyProperties(admin, loginDTO);

        //生产token
        String token = TokenUtils.genToken(String.valueOf(admin.getId()), admin.getPassword());
        loginDTO.setToken(token);
        return loginDTO;
    }

    @Override
    public void changePass(PasswordRequest request) {
        // 对旧密码和新密码进行加密
        String encryptedOldPass = securePass(request.getPassword());
        String encryptedNewPass = securePass(request.getNewPass());

        // 检查旧密码是否正确
        request.setPassword(encryptedOldPass); // 设置加密后的旧密码
        int matchCount = adminMapper.checkPassword(request);
        if (matchCount == 0) {
            throw new ServiceException("旧密码错误");
        }

        // 更新密码
        request.setNewPass(encryptedNewPass); // 设置加密后的新密码
        int count = adminMapper.updatePassword(request);
        if (count == 0) {
            throw new ServiceException("修改密码失败");
        }
    }

    @Override
    public void signup(Admin obj) {
        // 检查用户名是否已存在
        if (isUserNameExists(obj.getUsername())) {
            throw new ServiceException("用户名已存在！");
        }
        //默认密码123
        if (StrUtil.isBlank(obj.getPassword())){
            obj.setPassword(DEFAULT_PASSWORD);
        }
        obj.setPassword(securePass(obj.getPassword()));//设置md5加密，加盐
        adminMapper.save(obj);
    }

    //加盐方法
    private String securePass(String password){
        return SecureUtil.md5(password + PASS_SALT);
    }

    //检查用户名是否已经存在
    private  boolean isUserNameExists(String username) {
        return adminMapper.getByAdminname(username) != null;
    }
    //检查用户名是否已经存在（排除自身）
    private boolean isUserNameExistsExcludingSelf(String username, Integer id) {
    Admin existingAdmin = adminMapper.getByAdminname(username);
    return existingAdmin != null && !existingAdmin.getId().equals(id);
    }


}
