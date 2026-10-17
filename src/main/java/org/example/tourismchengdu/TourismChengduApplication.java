package org.example.tourismchengdu;

import org.example.tourismchengdu.config.JwtProperties;
import org.example.tourismchengdu.config.UploadProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("org.example.tourismchengdu.mapper")
@EnableConfigurationProperties({JwtProperties.class, UploadProperties.class})
public class TourismChengduApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourismChengduApplication.class, args);
    }

}
