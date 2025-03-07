   package com.example.springboot.controller;

   import com.example.springboot.common.Result;
   import com.example.springboot.controller.request.UserPageRequest;
   import com.example.springboot.entity.User;
   import com.example.springboot.service.IUserService;
   import com.github.pagehelper.PageInfo;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.*;

   import java.util.List;

   @CrossOrigin(origins = "http://localhost:8080")
   @RestController
   @RequestMapping("/user")
   public class UserController {

       private static final Logger logger = LoggerFactory.getLogger(UserController.class);

       @Autowired
       private IUserService userService;

       @PostMapping("/save")
       public Result<Void> save(@RequestBody User user) {
           userService.save(user);
           return Result.success(null);
       }

       @DeleteMapping("/delete/{id}")
       public Result<Void> delete(@PathVariable Integer id) {
           userService.deleteById(id);
           return Result.success(null);
       }

       @PutMapping("/update")
       public Result<Void> update(@RequestBody User user) {
           userService.update(user);
           return Result.success(null);
       }

       @PostMapping("/account")
       public Result<Void> account(@RequestBody User user) {
           userService.handleAccount(user);
           return Result.success(null);
       }

       @GetMapping("/{id}")
       public Result<User> getById(@PathVariable Integer id) {
           User user = userService.getById(id);
           return Result.success(user);
       }

       @GetMapping("/list")
       public Result<List<User>> list() {
           List<User> users = userService.list();
           return Result.success(users);
       }

       @GetMapping("/page")
       public Result<PageInfo<User>> page(UserPageRequest pageRequest) {
           try {
               // 检查 pageNum 和 pageSize 是否有效
               if (pageRequest.getPageNum() <= 0 || pageRequest.getPageSize() <= 0) {
                   return Result.error("页码和每页大小必须大于0");
               }

               PageInfo<User> pageInfo = userService.page(pageRequest);
               return Result.success(pageInfo);
           } catch (Exception e) {
               logger.error("查询失败", e);
               return Result.error("查询失败: " + e.getMessage());
           }
       }
   }
   