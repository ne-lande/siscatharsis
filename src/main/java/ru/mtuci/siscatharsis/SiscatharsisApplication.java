package ru.mtuci.siscatharsis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = "ru.mtuci.siscatharsis", exclude = { SecurityAutoConfiguration.class })
public class SiscatharsisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiscatharsisApplication.class, args);
	}

}
