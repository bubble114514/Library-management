package com.example.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.Result;
import com.example.springboot.controller.request.BookPageRequest;
import com.example.springboot.entity.Admin;
import com.example.springboot.entity.Book;
import com.example.springboot.service.IBookService;
import com.example.springboot.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/book")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    IBookService bookService;

    private static final String BASE_FILE_PATH = System.getProperty("user.dir") + "/files/";

    @PostMapping("/file/upload")
public Result<String> uploadFile(MultipartFile file) {
    if (file.isEmpty()) {
        return Result.error("上传文件不能为空");
    }

    // 定义最大文件大小为5MB
    final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // 检查文件大小是否超过最大值
    if (file.getSize() > MAX_FILE_SIZE) {
        return Result.error("上传文件不能超过5MB");
    }

    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
        return Result.error("文件名不能为空");
    }

    long flag = System.currentTimeMillis();
    String filePath = BASE_FILE_PATH + flag + "_" + originalFilename;
    try {
        FileUtil.mkParentDirs(filePath); // 创建父级目录
        file.transferTo(FileUtil.file(filePath));
        Admin currentAdmin = TokenUtils.getCurrentAdmin();
        String token = TokenUtils.genToken(currentAdmin.getId().toString(), currentAdmin.getPassword(), 15);
        String url = "http://localhost:9090/api/book/file/download/" + flag + "?token=" + token;
        if (originalFilename.endsWith("pdf") || originalFilename.endsWith("jpg") || originalFilename.endsWith("png")) {
            url += "&play=1";
        }
        return Result.success(url);
    } catch (Exception e) {
        log.info("文件上传失败", e);
    }
    return Result.error("文件上传失败");
}


    @GetMapping("/file/download/{flag}")
    public void downloadFile(@PathVariable String flag, @RequestParam(required = false) String play, HttpServletResponse response) {
        // 检查路径是否存在并且是一个目录
        File basePath = new File(BASE_FILE_PATH);
        if (!basePath.exists() || !basePath.isDirectory()) {
            log.error("路径不存在或不是一个目录: {}", BASE_FILE_PATH);
            throw new RuntimeException("路径不存在或不是一个目录");
        }
        OutputStream os;
        List<String> fileNames = FileUtil.listFileNames(BASE_FILE_PATH);
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        try {
            if (StrUtil.isNotEmpty(fileName)) {
                String realName = fileName.substring(fileName.indexOf("_") + 1); //获取文件名(根据下划线截取)
                if ("1".equals(play)) {//预览下载
                    response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(realName, StandardCharsets.UTF_8));
                } else {//附件下载
                    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(realName, StandardCharsets.UTF_8));
                }
                byte[] bytes = FileUtil.readBytes(BASE_FILE_PATH + File.separator + fileName);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            log.error("文件下载失败", e);
        }
    }

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
