package com.example.springboot.controller.request;

import lombok.Data;

@Data
public class UserPageRequest extends BaseRequest {
    private String name;
    private String phone;

    // 添加 getPageNum 和 getPageSize 方法
    public int getPageNum() {
        return super.getPageNum();
    }

    public int getPageSize() {
        return super.getPageSize();
    }
}
