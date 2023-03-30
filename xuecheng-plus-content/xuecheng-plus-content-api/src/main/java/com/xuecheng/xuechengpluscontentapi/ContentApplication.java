package com.xuecheng.xuechengpluscontentapi;


import com.spring4all.swagger.EnableSwagger2Doc;
import com.sun.tracing.dtrace.ArgsAttributes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSwagger2Doc
public class ContentApplication{
    public static void main(String[] args){
        SpringApplication.run(ContentApplication.class , args);
    }
}
