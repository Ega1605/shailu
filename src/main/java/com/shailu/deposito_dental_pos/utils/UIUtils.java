package com.shailu.deposito_dental_pos.utils;

import javafx.scene.Cursor;
import javafx.scene.Node;

public class UIUtils {

    public static void applyHoverEffect(Node node) {
        node.setCursor(Cursor.HAND);
        node.setOpacity(0.54);

        node.setOnMouseEntered(e -> {
            node.setOpacity(1.0);
        });

        node.setOnMouseExited(e -> {
            node.setOpacity(0.54);
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });

        node.setOnMousePressed(e -> {
            node.setScaleX(0.85);
            node.setScaleY(0.85);
        });

        node.setOnMouseReleased(e -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }
}
