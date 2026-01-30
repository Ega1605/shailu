package com.shailu.deposito_dental_pos.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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
            throw new RuntimeException("Error loading screen" + fxml, e);
        }
    }

    public <T> Optional<T> showDialog(
            String fxml,
            String title,
            Consumer<Object> initializer,
            Function<Object,  Optional<T>> resultMapper
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            // create window modal
            Stage dialogStage = new Stage();
            dialogStage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/Shailu.jpeg"))
            );
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Block main window
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));

            Object controller = loader.getController();

            // 1. Set Stage to controllerr
            try {
                controller.getClass().getMethod("setDialogStage", Stage.class).invoke(controller, dialogStage);
            } catch (NoSuchMethodException e) {
                System.out.println("Error controller");
            }


            if (initializer != null) {
                initializer.accept(controller);
            }

            dialogStage.showAndWait();

            return resultMapper.apply(controller);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
