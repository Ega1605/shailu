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

    public void show(String fxml, String title) {
        show(fxml, title, true);
    }

    public void show(String fxml, String title, boolean maximize) {
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
            stage.setTitle(title);
            if (maximize) {
                stage.setMaximized(true);
                stage.setResizable(true);
            } else {
                stage.setMaximized(false);
                stage.setResizable(false); // avoid user changes size
                stage.sizeToScene();       // adjust edge for FXML size
                stage.centerOnScreen();
            }
            stage.show();

        } catch (Exception e) {
            e.getCause().printStackTrace();
            throw new RuntimeException("Error loading screen" + fxml, e);
        }
    }
}
