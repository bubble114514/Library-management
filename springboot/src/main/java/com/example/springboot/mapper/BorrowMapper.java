package com.example.springboot.mapper;


import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.ReturnBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BorrowMapper {

    List<Borrow> list();

    List<Borrow> listByCondition(BaseRequest baseRequest);

    List<ReturnBook> listReturnByCondition(BaseRequest baseRequest);

    void save(Borrow obj);
    
    void saveReturn(ReturnBook obj);

    Borrow getById(Integer id);

    void update(Borrow obj);

    void deleteById(Integer id);


    void deleteReturById(Integer id);

    void updateStatus(String status,Integer id);


}
