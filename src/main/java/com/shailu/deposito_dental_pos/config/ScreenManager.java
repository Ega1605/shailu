package com.shailu.deposito_dental_pos.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ScreenManager {

    private final ApplicationContext context;
    private Stage stage;

    public ScreenManager(ApplicationContext context) {
        this.context = context;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void show(String fxml, String title, boolean bigScreen) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/" + fxml)
            );
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/Shailu.jpeg"))
            );

            stage.setScene(new Scene(root));
            stage.setMaximized(bigScreen);
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.getCause().printStackTrace();
            throw new RuntimeException("Error cargando pantalla " + fxml, e);
        }
    }
}
