package com.xigua.miaosha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@RestController
@SpringBootApplication(scanBasePackages = {"com.xigua.miaosha"})
@MapperScan("com.xigua.miaosha.dao")
public class App {

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        SpringApplication.run(App.class,args);
    }
}
