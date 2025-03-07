package com.example.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

    // 健康检查窗口
    @GetMapping
    public String health() {
        return "SUCCESS";
    }

    @Controller
    public static class SPAController {

        @RequestMapping({"/{path:[^\\.]*}", "/{path:^(?!api).*$}"})
        public ModelAndView spa() {
            return new ModelAndView("forward:/index.html");
        }
    }
}