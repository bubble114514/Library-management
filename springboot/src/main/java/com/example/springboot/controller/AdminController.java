package com.example.springboot.controller.request;
//Controoller的作用：接收前端请求，调用service层，返回结果

import com.example.springboot.common.Result;
import com.example.springboot.entity.Admin;
import com.example.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final IUserService adminService;

    @Autowired
    public AdminController(IUserService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/save")
    public Result save(@RequestBody Admin admin) {

        adminService.save(admin);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        adminService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Admin admin){
        adminService.update(admin);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        Admin admin =adminService. getById(id);
        return Result.success(admin);

    }

    @GetMapping("/list")
    public Result list() {
        List<Admin> users = adminService.list();
        return Result.success(users);
    }

    @GetMapping("/page")
    public Result page(UserPageRequest userPageRequest) {
        return Result.success(adminService.page(userPageRequest));
    }
}
