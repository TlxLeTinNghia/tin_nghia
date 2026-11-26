package com.example.tin_nghia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

@SpringBootApplication
public class TinNghiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinNghiaApplication.class, args);
    }
    @Bean
    CommandLineRunner testConnection(DataSource dataSource) {
        return args -> {
            try (var connection = dataSource.getConnection()) {
                System.out.println(
                        "Đã kết nối database: " + connection.getCatalog()
                );
            }
        };
    }
}
