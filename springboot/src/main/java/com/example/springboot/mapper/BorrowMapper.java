package com.example.springboot.mapper;



import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Book;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {

    List<Book> list();

    List<Book> listByCondition(BaseRequest baseRequest);

    void save(Book obj);

    Book getById(Integer id);

    void update(Book obj);

    void deleteById(Integer id);

    List<Book> selectByPid(Integer id);


}
