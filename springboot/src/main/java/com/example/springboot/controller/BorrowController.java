package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.controller.request.BookPageRequest;
import com.example.springboot.entity.Book;
import com.example.springboot.service.IBookService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/book")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    IBookService bookService;
    

    @PostMapping("/save")
    public Result<Void> save(@RequestBody Book obj) {
        bookService.save(obj);
        return Result.success(null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        bookService.deleteById(id);
        return Result.success(null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Book obj) {
        bookService.update(obj);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Integer id) {
        Book book = bookService.getById(id);
        return Result.success(book);
    }

    @GetMapping("/list")
    public Result<List<Book>> list() {
        List<Book> list = bookService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageInfo<Book>> page(BookPageRequest pageRequest) {
        try {
            // 检查 pageNum 和 pageSize 是否有效
            if (pageRequest.getPageNum() <= 0 || pageRequest.getPageSize() <= 0) {
                return Result.error("页码和每页大小必须大于0");
            }

            PageInfo<Book> pageInfo = bookService.page(pageRequest);
            return Result.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
   