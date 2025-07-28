package com.wcs.travel_blog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TravelBlogApplication {

	public static void main(String[] args) {

//		Dotenv dotenv = Dotenv.load();
//
//		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
//		System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
//		System.setProperty("DATABASE_USER", dotenv.get("DATABASE_USER"));
//		System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));
//		System.setProperty("CORS_ALLOWED_ORIGIN", dotenv.get("CORS_ALLOWED_ORIGIN"));

		SpringApplication.run(TravelBlogApplication.class, args);

	}
}
