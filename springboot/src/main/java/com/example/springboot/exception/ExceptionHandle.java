package com.example.springboot.exception;
//ExceptionHandle：全局异常处理
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import com.example.springboot.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * 使用@RestControllerAdvice注解使其成为全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandle {

    /**
     * 处理所有未被捕获的异常
     * 当抛出Exception时，执行此方法
     *
     * @param e Exception实例
     * @return 包含系统错误信息的Result对象
     */
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionError(Exception e){
        // 记录异常信息
        log.error("系统错误", e);
        // 返回包含系统错误信息的错误结果
        return Result.error("系统错误");
    }
    /**
     * 处理ServiceException异常
     * 当抛出ServiceException时，执行此方法
     *
     * @param e ServiceException实例
     * @return 包含错误信息的Result对象
     */
    @ExceptionHandler(value = ServiceException.class)
    public Result<String> exceptionError(ServiceException e){
        // 记录异常信息
        log.error("业务异常", e);
        String code=e.getCode();
        if (StrUtil.isBlank(code)){
            return Result.error(code,e.getMessage());
        }
        // 返回包含异常信息的错误结果
        return Result.error(e.getMessage());
    }
}
