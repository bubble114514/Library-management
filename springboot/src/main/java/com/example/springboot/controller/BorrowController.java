package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.controller.request.BorrowPageRequest;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.ReturnBook;
import com.example.springboot.service.IBorrowService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/borrow")
public class BorrowController {

    private static final Logger logger = LoggerFactory.getLogger(BorrowController.class);

    @Autowired
    IBorrowService borrowService;
    

    @PostMapping("/save")
    public Result<Void> save(@RequestBody Borrow obj) {
        borrowService.save(obj);
        return Result.success(null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        borrowService.deleteById(id);
        return Result.success(null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Borrow obj) {
        borrowService.update(obj);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<Borrow> getById(@PathVariable Integer id) {
        Borrow borrow = borrowService.getById(id);
        return Result.success(borrow);
    }

    @GetMapping("/list")
    public Result<List<Borrow>> list() {
        List<Borrow> list = borrowService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageInfo<Borrow>> page(BorrowPageRequest pageRequest) {
        try {
            // 检查 pageNum 和 pageSize 是否有效
            if (pageRequest.getPageNum() <= 0 || pageRequest.getPageSize() <= 0) {
                return Result.error("页码和每页大小必须大于0");
            }
            PageInfo<Borrow> pageInfo = borrowService.page(pageRequest);
            return Result.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/pageRetur")
    public Result<PageInfo<ReturnBook>> pageRetur(BorrowPageRequest pageRequest) {
        return Result.success(borrowService.pageReturnBook(pageRequest));
    }

    @PostMapping("/saveRetur")
    public Result<Void> saveRetur(@RequestBody ReturnBook obj) {
        borrowService.saveReturnBook(obj);
        return Result.success(null);
    }
    @DeleteMapping("/deleteRetur/{id}")
    public Result<Void> deleteRetur(@PathVariable Integer id) {
        borrowService.deleteReturById(id);
        return Result.success(null);
    }
}
   