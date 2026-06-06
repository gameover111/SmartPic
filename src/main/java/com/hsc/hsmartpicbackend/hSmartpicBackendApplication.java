package com.hsc.hsmartpicbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.hsc.hsmartpicbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class hSmartpicBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(hSmartpicBackendApplication.class, args);
    }

}
