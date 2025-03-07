package com.example.springboot.service.impl;


import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Book;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.ReturnBook;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.BookMapper;
import com.example.springboot.mapper.BorrowMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IBorrowService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
public class BorrowService implements IBorrowService {
    @Resource
    BorrowMapper borrowMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BORROW_LOCK_PREFIX = "borrow_log";//借书锁键前缀
    private static final String RETURN_LOCK_PREFIX = "return_log";//还书锁键前缀
    private static final int LOCK_EXPIRE_TIME = 10; //锁的过期时间

    //引入线程池
    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }

    @Async("asyncExecutor")
    public void asyncSaveLog(Borrow obj) {
        log.info("异步保存借书记录：{}", obj);
    }

    @Async("asyncExecutor")
    public void asyncSaveReturnLog(ReturnBook obj) {
        log.info("异步保存还书记录：{}", obj);
    }

    @Override
    public List<Borrow> list() {
        return borrowMapper.list();
    }

    @Override
    public PageInfo<Borrow> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
        //自关联查询
        List<Borrow> borrows = borrowMapper.listByCondition(baseRequest);
        for (Borrow borrow : borrows) {
            LocalDate returnDate = borrow.getReturnDate();
            LocalDate now = LocalDate.now();
            if (now.plusDays(1).isEqual(returnDate)) {
                borrow.setNote("即将到期");
            } else if (now.isBefore(returnDate)) {
                borrow.setNote("正常");
            } else if (now.isEqual(returnDate)) {
                borrow.setNote("已到期");
            } else if (now.isAfter(returnDate)) {
                borrow.setNote("已超期");
            }
        }


        return new PageInfo<>(borrows);
    }

    @Override
    public PageInfo<ReturnBook> pageReturnBook(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
        List<ReturnBook> returnBooks = borrowMapper.listReturnByCondition(baseRequest);
        return new PageInfo<>(returnBooks);
    }

    @Override
    public void save(Borrow obj) {
        //设置锁键（借书锁键前缀+用户ID+图书标准码）
        String lockKey = BORROW_LOCK_PREFIX + obj.getUserId() + ":" + obj.getBookNo();
        boolean locked = tryLock(lockKey);
        if (!locked) {
            throw new ServiceException("操作正在处理中，请勿重复提交");
        }
        try {
            // 1. 校验用户积分是否足够
            validateUserScore(obj);

            // 2. 校验图书库存是否足够
            validateBookStock(obj);

            // 3. 更新用户积分余额
            updateUserAccount(obj);

            // 4. 更新图书库存
            updateBookStock(obj);

            // 5. 设置借书相关信息
            setBorrowDetails(obj);

            // 6. 提交借书记录
            borrowMapper.save(obj);
            //异步记录日志
            asyncSaveLog(obj);
        } finally {
            releaseLock(lockKey);
        }
    }

    // 校验用户积分是否足够
    private void validateUserScore(Borrow obj) {
        String userId = obj.getUserId();
        User user = userMapper.getByUserName(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (user.getAccount() < obj.getScore()) {
            throw new ServiceException("用户积分不足");
        }
    }

    // 校验图书库存是否足够
    private void validateBookStock(Borrow obj) {
        Book book = bookMapper.getByBookNo(obj.getBookNo());
        if (book == null) {
            throw new ServiceException("所借图书不存在");
        }
        if (book.getNums() < 1) {
            throw new ServiceException("图书数量不足");
        }
    }

    // 更新用户积分余额
    private void updateUserAccount(Borrow obj) {
        int rowsUser = userMapper.decrAccount(obj.getUserId(), obj.getScore());
        if (rowsUser <= 0) {
            throw new ServiceException("积分不足或用户不存在");
        }
    }

    // 更新图书库存
    private void updateBookStock(Borrow obj) {
        int rowsBook = bookMapper.decrStock(obj.getBookNo());
        if (rowsBook <= 0) {
            throw new ServiceException("图书库存不足");
        }
    }

    // 设置借书相关信息
    private void setBorrowDetails(Borrow obj) {
        obj.setReturnDate(LocalDate.now().plusDays(obj.getDays()));
        obj.setStatus("已借出");
        obj.setScore(obj.getScore()); // 确保所需积分被正确设置
    }


    //还书逻辑
    @Transactional
    @Override
    public void saveReturnBook(ReturnBook obj) {
        //设置锁键（借书锁键前缀+用户ID+图书标准码）
        String lockKey = RETURN_LOCK_PREFIX + obj.getUserId() + ":" + obj.getBookNo();
        boolean locked = tryLock(lockKey);
        if (!locked) {
            throw new ServiceException("操作正在处理中，请勿重复提交");
        }
        try {
            //改状态
            obj.setStatus("已归还");
            borrowMapper.updateStatus(obj.getStatus(), obj.getId()); // obj.getId() 是前端传来的借书id

            //图书数量增加
            bookMapper.updateNumByBookNo(obj.getBookNo());
            //更新实际归还日期
            obj.setRealDate(LocalDate.now());
            borrowMapper.saveReturn(obj);

            //返还和扣除用户积分
            Book book = bookMapper.getByBookNo(obj.getBookNo());
            if (book != null) {
                long until = 0;
                if (obj.getRealDate().isBefore(obj.getReturnDate())) {
                    until = obj.getRealDate().until(obj.getReturnDate(), ChronoUnit.DAYS);
                } else if (obj.getRealDate().isAfter(obj.getReturnDate())) {//逾期归还，扣除额外积分
                    until = -obj.getReturnDate().until(obj.getRealDate(), ChronoUnit.DAYS);
                }
                int score = (int) until * book.getScore();//获取待归还的积分
                User user = userMapper.getByUserName(obj.getUserId());
                int account = user.getAccount() + score;
                user.setAccount(account);
                if (account < 0) {//积分不够扣
                    //锁定账号
                    user.setStatus(false);
                }
                userMapper.update(user);
                //异步记录日志
                asyncSaveReturnLog(obj);
            }

        } finally {
            releaseLock(lockKey);
        }
    }

    @Override
    public Borrow getById(Integer id) {
        return borrowMapper.getById(id);
    }

    @Override
    public void update(Borrow obj) {
        borrowMapper.update(obj);
    }

    @Override
    public void deleteById(Integer id) {
        borrowMapper.deleteById(id);
    }

    @Override
    public void deleteReturById(Integer id) {
        borrowMapper.deleteReturById(id);
    }

    private boolean tryLock(String lockKey) {
        //尝试获取锁
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        //设置锁及其过期时间
        Boolean locked = ops.setIfAbsent(lockKey, "locked", LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
        //如果锁已存在，则返回null
        return locked != null && locked;
    }
    private void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }

}
