package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.AdminPageRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.PasswordRequest;
import com.example.springboot.entity.Admin;
import com.example.springboot.service.IAdminService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    IAdminService adminService;

    @PostMapping("/login")
    public Result<LoginDTO> login(@RequestBody LoginRequest request) {
        LoginDTO login = adminService.login(request);
        return Result.success(login);
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody Admin obj) {
        adminService.save(obj);
        return Result.success(null);
    }
    @PostMapping("/signup")
    public Result<Void> signup(@RequestBody Admin obj) {
        adminService.signup(obj);
        return Result.success(null);
    }

    @PutMapping("/password")
    public Result<Void> password(@RequestBody PasswordRequest request) {
        adminService.changePass(request);
        return Result.success(null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        adminService.deleteById(id);
        return Result.success(null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Admin obj) {
        adminService.update(obj);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<Admin> getById(@PathVariable Integer id) {
        Admin admin = adminService.getById(id);
        return Result.success(admin);
    }

    @GetMapping("/list")
    public Result<List<Admin>> list() {
        List<Admin> list = adminService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageInfo<Admin>> page(AdminPageRequest pageRequest) {
        try {
            // 检查 pageNum 和 pageSize 是否有效
            if (pageRequest.getPageNum() <= 0 || pageRequest.getPageSize() <= 0) {
                return Result.error("页码和每页大小必须大于0");
            }

            PageInfo<Admin> pageInfo = adminService.page(pageRequest);
            return Result.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
   