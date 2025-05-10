package client;

import common.Network.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoomWaitingScene {
    private static Stage stage;
    private static NetworkManager network;
    private static String roomId;
    private static boolean isReady;

    public static void show(Stage primaryStage, NetworkManager net, String id) {
        stage = primaryStage;
        network = net;
        roomId = id;
        isReady = false;

        updateUI(network.getCurrentRoom());
    }

    public static void onRoomUpdate(RoomUpdate update) {
        Platform.runLater(() -> {
            if (stage != null) { // Kiểm tra stage trước khi cập nhật
                updateUI(update);
            }
        });
    }

    private static void updateUI(RoomUpdate update) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label roomLabel = new Label("Room ID: " + roomId);
        Label playersLabel = new Label("Players:");
        if (update != null) {
            for (PlayerInfo p : update.players) {
                playersLabel.setText(playersLabel.getText() + "\n- " + p.username);
            }
        }

        Button readyButton = new Button(isReady ? "Ready" : "Not Ready");
        readyButton.setDisable(update == null || !update.canStart);
        readyButton.setOnAction(e -> ready(readyButton));

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            network.leaveRoom();
            RoomListScene.show(stage, network);
        });

        root.getChildren().addAll(roomLabel, playersLabel, readyButton, exitButton);
        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene); // stage đã được gán trong show(), nên không null tại đây
    }

    public static void ready(Button b) {
        isReady = !isReady;
        b.setText(isReady ? "Ready" : "Not Ready");
        network.requestReady(isReady);
    }
    public static void startGame(String chessColor) {
        Platform.runLater(() -> GamePlayScene.show(stage, network, chessColor));
    }
}