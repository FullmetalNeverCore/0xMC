package com.mcl;

import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.util.List;

import javafx.concurrent.Task;
import javafx.application.Platform;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class UIBuildingBlocks {
    public static VBox createBox() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }

    public static Text createText(String text) {
        Text textBox = new Text(text);
        return textBox;
    }

    public static TextField createTextField() {
        TextField uname = new TextField();
        uname.setPrefWidth(100);
        return uname;
    }
}
