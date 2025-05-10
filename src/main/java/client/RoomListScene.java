package client;

import common.Network.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoomListScene {
    private static Stage stage;
    private static NetworkManager network;
    private static ListView<String> roomListView;
    private static ListView<String> playerListView;

    public static void show(Stage primaryStage, NetworkManager net) {
        stage = primaryStage;
        network = net;

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label roomLabel = new Label("Available Rooms:");
        roomListView = new ListView<>();
        roomListView.setPrefHeight(200);
        roomListView.setPrefWidth(300);

        Label playerLabel = new Label("Online Players:");
        playerListView = new ListView<>();
        playerListView.setPrefHeight(200);
        playerListView.setPrefWidth(300);

        TextField roomIdField = new TextField();
        roomIdField.setPromptText("Enter Room ID");
        Button joinButton = new Button("Join Room");
        Button viewButton = new Button("View");
        Button createButton = new Button("Create Room");
        Button backButton = new Button("Exit");

        joinButton.setOnAction(e -> {
            String selectedRoom = roomListView.getSelectionModel().getSelectedItem();
            String roomId = selectedRoom != null ? selectedRoom.split(" - ")[0] : roomIdField.getText();
            if (!roomId.isEmpty()) {
                network.joinRoom(roomId);
            }
        });

        viewButton.setOnAction(e -> {
            String selectedRoom = roomListView.getSelectionModel().getSelectedItem();
            String roomId = selectedRoom != null ? selectedRoom.split(" - ")[0] : roomIdField.getText();
            if (!roomId.isEmpty()) {
                network.viewRoom(roomId);
            }
        });
        createButton.setOnAction(e -> network.createRoom());

        backButton.setOnAction(e -> {
            Platform.runLater(() -> stage.close());
        });

        VBox roomBox = new VBox(10, roomLabel, roomListView);
        VBox playerBox = new VBox(10, playerLabel, playerListView);
        HBox listviewBox = new HBox(10, playerBox, roomBox);
        HBox buttonBox = new HBox(10, joinButton, viewButton, createButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(listviewBox, roomIdField, buttonBox);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();

        network.requestRoomList();
        network.requestPlayerList();// Yêu cầu ban đầu
    }

    public static void onRoomListReceived(RoomListResponse response) {
        if (roomListView != null) { // Chỉ cập nhật nếu giao diện đang hiển thị
            Platform.runLater(() -> {
                roomListView.getItems().clear();
                for (RoomInfo room : response.rooms) {
                    String roomInfo = room.roomId + " - " + room.players.size() + "/2 players";
                    roomListView.getItems().add(roomInfo);
                }
            });
        }
    }

    public static void onPlayerListReceived(PlayerListResponse response) {
        if (playerListView != null) {
            Platform.runLater(() -> {
                playerListView.getItems().clear();
                for (PlayerInfo player : response.players) {
                    String playerInfo = "ID: " + player.id + " - " + player.username;
                    playerListView.getItems().add(playerInfo);
                }
            });
        }
    }

    public static void onRoomCreated(String roomId) {
        Platform.runLater(() -> RoomWaitingScene.show(stage, network, roomId));
    }

    public static void onJoinSuccess() {
        if (network != null && network.getCurrentRoom() != null) {
            Platform.runLater(() -> RoomWaitingScene.show(stage, network, network.getCurrentRoomId()));
        }
    }

    public static void onViewRoom(ViewerResponse response) {
        Platform.runLater(() -> {if (response.gameStarted) {
            GameViewScene.show(stage, network, response.initialBoard);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Room has not been started");
            alert.showAndWait();
        }});
    }
}