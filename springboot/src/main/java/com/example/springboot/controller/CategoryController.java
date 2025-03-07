package com.example.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import com.example.springboot.common.Result;
import com.example.springboot.controller.request.CategoryPageRequest;
import com.example.springboot.entity.Category;
import com.example.springboot.service.ICategoryService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    //获取分类名称数据供book使用
    @GetMapping("/tree")
    public Result<List<Category>> tree() {
        List<Category> list = categoryService.list();

        //对list操作即可
        //List<Category> treeList = list.stream().filter(v -> v.getPid() == null).toList();//第一层

        //递归实现children列表的查询
        return Result.success(createTree(null, list)); // null 表示从第一级开始递归
    }

    //完全递归的方法实现递归树
    private List<Category> createTree(Integer pid, List<Category> categories) {
        List<Category> treeList = new ArrayList<>();
        for (Category category : categories) {
            if (pid == null) {
                if (category.getPid() == null) { // 顶级分类
                    category.setChildren(createTree(category.getId(), categories)); // 递归加载子分类
                    treeList.add(category);
                }
            } else {
                if (pid.equals(category.getPid())) { // 子分类
                    category.setChildren(createTree(category.getId(), categories)); // 递归加载子分类
                    treeList.add(category);
                }
            }
        }
        // 如果 children 为空，设置为 null
        treeList.forEach(category -> {
            if (CollUtil.isEmpty(category.getChildren())) {
                category.setChildren(null);
            }
        });
        return treeList;
    }


    @GetMapping("/page")
    public Result<PageInfo<Category>> page(CategoryPageRequest pageRequest) {
        try {
            // 检查 pageNum 和 pageSize 是否有效
            if (pageRequest.getPageNum() <= 0 || pageRequest.getPageSize() <= 0) {
                return Result.error("页码和每页大小必须大于0");
            }

            PageInfo<Category> pageInfo = categoryService.page(pageRequest);
            List<Category> list = pageInfo.getList();
            List<Category> treeList = createTree(null, list); // 递归构建树形结构
            pageInfo.setList(treeList);
            logger.info("返回的树形结构数据: {}", treeList); // 添加日志
            return Result.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }


}