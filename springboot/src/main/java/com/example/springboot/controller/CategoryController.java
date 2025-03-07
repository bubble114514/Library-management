package com.example.springboot.controller.request;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Category;
import com.example.springboot.service.ICategoryService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/category")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    ICategoryService categoryService;
    

    @PostMapping("/save")
    public Result<Void> save(@RequestBody Category obj) {
        categoryService.save(obj);
        return Result.success(null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        categoryService.deleteById(id);
        return Result.success(null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Category obj) {
        categoryService.update(obj);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Integer id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        List<Category> list = categoryService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageInfo<Category>> page(AdminPageRequest pageRequest) {
        try {
            // 检查 pageNum 和 pageSize 是否有效
            if (pageRequest.getPageNum() <= 0 || pageRequest.getPageSize() <= 0) {
                return Result.error("页码和每页大小必须大于0");
            }

            PageInfo<Category> pageInfo = categoryService.page(pageRequest);
            return Result.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
   