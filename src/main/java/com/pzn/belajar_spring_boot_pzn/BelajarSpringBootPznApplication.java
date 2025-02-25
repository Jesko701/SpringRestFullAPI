package com.pzn.belajar_spring_boot_pzn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AppConfig.class})
public class BelajarSpringBootPznApplication {

	public static void main(String[] args) {
		SpringApplication.run(BelajarSpringBootPznApplication.class, args);
	}

}
