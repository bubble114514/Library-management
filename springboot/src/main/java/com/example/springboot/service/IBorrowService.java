package com.example.springboot.service;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.BorrowPageRequest;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.ReturnBook;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IBorrowService {
    List<Borrow> list();

    PageInfo<Borrow> page(BaseRequest baseRequest);

    PageInfo<ReturnBook> pageReturnBook(BaseRequest baseRequest);

    void save(Borrow obj);

    void saveReturnBook(ReturnBook obj);

    Borrow getById(Integer id);

    void update(Borrow obj);

    void deleteById(Integer id);

    void deleteReturById(Integer id);

}
