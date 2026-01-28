package br.com.inproutservices.faturamento_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FaturamentoApplication {
    public static void main(String[] args) {
        SpringApplication.run(FaturamentoApplication.class, args);
    }
}