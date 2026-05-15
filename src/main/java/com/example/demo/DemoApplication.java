package com.example.demo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
			if (entry.getKey().contains("KEY")) {
				System.out.println("Loaded " + entry.getKey() + ": " + entry.getValue().substring(0, 5) + "...");
			}
		});
		
		SpringApplication.run(DemoApplication.class, args);
	}

}
