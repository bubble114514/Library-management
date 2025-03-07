   package com.example.springboot.service.impl;
//Service的作用：聚合数据访问层对象，对数据进行统一处理，简化业务逻辑层对象和数据访问层的交互。
   import cn.hutool.core.date.DateUtil;
   import cn.hutool.core.util.IdUtil;
   import com.example.springboot.controller.request.BaseRequest;
   import com.example.springboot.controller.request.UserPageRequest;
   import com.example.springboot.entity.User;
   import com.example.springboot.mapper.UserMapper;
   import com.example.springboot.service.IUserService;
   import com.github.pagehelper.PageHelper;
   import com.github.pagehelper.PageInfo;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.stereotype.Service;

   import java.util.Date;
   import java.util.List;

   @Service
   public class UserService implements IUserService {

       @Autowired
       UserMapper userMapper;

       @Override
       public List<User> list() {
           return userMapper.list();
       }

       @Override
       public PageInfo<User> page(BaseRequest baseRequest) {
           if (baseRequest == null) {
               throw new IllegalArgumentException("UserPageRequest cannot be null");
           }
           PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
           List<User> users = userMapper.listByCondition(baseRequest);
           return new PageInfo<>(users);
       }

       @Override
       public void save(User user) {
           Date date = new Date();
           // 当做卡号来处理
           user.setUsername(DateUtil.format(date, "yyyyMMdd") + Math.abs(IdUtil.fastSimpleUUID().hashCode()));
           userMapper.save(user);
       }

       @Override
       public User getById(Integer id) {
           return userMapper.getById(id);
       }

       @Override
       public void update(User user) {
           userMapper.update(user);
       }

       @Override
       public void deleteById(Integer id) {
           userMapper.deleteById(id);
       }
   }
   