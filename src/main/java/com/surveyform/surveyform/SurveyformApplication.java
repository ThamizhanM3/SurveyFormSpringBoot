package com.surveyform.surveyform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SurveyformApplication {

	public static void main(String[] args) {
		System.out.println("Strating...");
		SpringApplication.run(SurveyformApplication.class, args);
		System.out.println("Started");
	}

}
