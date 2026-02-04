package com.shailu.deposito_dental_pos;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		//SpringApplication.run(Main.class, args);
		//Start JavaFX
		Application.launch(JavaFxApplication.class, args);
	}

}
