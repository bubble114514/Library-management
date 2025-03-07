package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
@Data
public class Borrow {
    private Integer id;
    private String bookName;
    private String bookNo;
    private String userId;
    private String userName;
    private String userPhone;
    private Integer score;
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private LocalDate createtime;
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private LocalDate updatetime;
    private String status;
    private Integer days;
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private LocalDate returnDate;

    //提醒转台即将到期（-1） 已到期（当天） 已过期（超出归还日期）
    private String note;

}


