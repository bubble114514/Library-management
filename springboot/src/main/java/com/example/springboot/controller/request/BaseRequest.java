package com.example.springboot.controller.request;

import lombok.Data;

@Data
public class BaseRequest {
    private int pageNum = 1;
    private int pageSize = 10;

}
