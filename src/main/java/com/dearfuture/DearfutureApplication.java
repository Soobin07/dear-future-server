package com.dearfuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DearfutureApplication {

	public static void main(String[] args) {
		SpringApplication.run(DearfutureApplication.class, args);
	}

}
