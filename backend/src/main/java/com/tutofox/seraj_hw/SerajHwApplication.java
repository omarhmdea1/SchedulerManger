package com.tutofox.seraj_hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class SerajHwApplication {

	public static void main(String[] args) {
		SpringApplication.run(SerajHwApplication.class, args);
	}

}
