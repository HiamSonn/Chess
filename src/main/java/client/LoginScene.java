package client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScene {
    private static Stage stage;
    private static NetworkManager network;

    public static void show(Stage primaryStage, NetworkManager net) {
        stage = primaryStage;
        network = net;

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label label = new Label("Enter Username:");
        TextField usernameField = new TextField();
        usernameField.setMaxWidth(200);
        Button loginButton = new Button("Login");

        loginButton.setOnAction(e -> network.sendLogin(usernameField.getText()));

        root.getChildren().addAll(label, usernameField, loginButton);
        Scene scene = new Scene(root, 350, 250);
        stage.setScene(scene);
        stage.show();
    }

    public static void onLoginSuccess() {
        Platform.runLater(() -> RoomListScene.show(stage, network));
    }
}