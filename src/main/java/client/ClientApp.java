package client;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    private NetworkManager network;

    @Override
    public void start(Stage primaryStage) throws IOException {
        network = new NetworkManager();
        primaryStage.setTitle("My Game");
        LoginScene.show(primaryStage, network);
    }

    public static void main(String[] args) {
        launch(args);
    }
}