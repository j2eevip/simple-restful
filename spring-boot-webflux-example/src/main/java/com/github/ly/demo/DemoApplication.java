package com.github.ly.demo;

import com.github.ly.sr.EnableSimpleRestful;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 由于是源代码调试，需要加这个，如果是使用jar包则不需要加这个，可以直接加载
@EnableSimpleRestful
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DemoApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

}
