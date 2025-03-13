package com.example.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Book;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.BookMapper;
import com.example.springboot.service.IBookService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BookService implements IBookService {
    @Resource
    BookMapper bookMapper;

    @Override
    public List<Book> list() {
        return bookMapper.list();
    }

    @Override
    public PageInfo<Book> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
        //自关联查询
        List<Book> books = bookMapper.listByCondition(baseRequest);
        return new PageInfo<>(books);
    }

    @Override
    public void save(Book obj) {
        obj.setCategory(category(obj.getCategories()));
        // 检查用户名是否已存在
        if (isUserNameExists(obj.getBookNo())) {
            throw new ServiceException("该图书已存在！");
        }else{
            bookMapper.save(obj);
        }
    }


    @Override
    public Book getById(Integer id) {
        return bookMapper.getById(id);
    }

    @Override
    public void update(Book obj) {
        obj.setCategory(category(obj.getCategories()));
        bookMapper.update(obj);
    }

    @Override
    public void deleteById(Integer id) {
        bookMapper.deleteById(id);
    }

    private String category(List<String> categories){
        StringBuilder sb = new StringBuilder();
        if (CollUtil.isNotEmpty(categories)) {
            categories.forEach(v -> sb.append(v).append(">"));
            return sb.substring(0, sb.lastIndexOf(">"));
        }
        return sb.toString();

    }
    //检查图书是否已经存在
    private  boolean isUserNameExists(String bookNo) {
        return bookMapper.getByBookNo(bookNo) != null;
    }


}
