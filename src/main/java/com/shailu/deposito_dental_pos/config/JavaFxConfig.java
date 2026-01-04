package com.shailu.deposito_dental_pos.config;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaFxConfig {

    private final ApplicationContext context;

    public JavaFxConfig(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public FXMLLoader fxmlLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);//Cuando cargues un controlador, pídeselo a Spring
        return loader;
    }
}
