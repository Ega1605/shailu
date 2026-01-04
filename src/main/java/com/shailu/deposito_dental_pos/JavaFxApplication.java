package com.shailu.deposito_dental_pos;

import com.shailu.deposito_dental_pos.config.ScreenManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext context;
    //Une Spring con JavaFX

    //carga todo Spring
    @Override
    public void init() {
        context = new SpringApplicationBuilder(SpringBootApp.class)
                .run();
    }
    //empieza el lo visual
    @Override
    public void start(Stage stage) throws Exception {

        ScreenManager screenManager = context.getBean(ScreenManager.class);
        screenManager.setStage(stage);

        screenManager.show("login.fxml", "Login");

    }
    //Cierra Spring cuando se cierra la ventana
    @Override
    public void stop() {
        context.close();
    }
}
