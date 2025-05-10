package client;

import common.Network.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

import static common.Chess.checkMovement;

public class GamePlayScene {
    private static Stage stage;
    private static NetworkManager network;
    private static TextArea chatArea;
    private static Pane boardChessPane;

    private static final int BOARD_WIDTH = 9;  // 9 cột
    private static final int BOARD_HEIGHT = 10; // 10 hàng
    private static final double CELL_SIZE = 50;
    private static String[][] initialBoard;
    private static Circle[][] pieces;
    private static Circle choosenPiece;// Lưu tữ quân cờ
    private static ArrayList<Circle> movementPieces = new ArrayList<>();
    private static boolean yourTurn;

    public static void show(Stage primaryStage, NetworkManager net, String chessColor) {
        stage = primaryStage;
        network = net;
        yourTurn = (chessColor.equals("red")) ? true : false;

        // main layout
        HBox root = new HBox(10);
        root.setPadding(new Insets(10));

        // game area
        boardChessPane = new Pane();
        boardChessPane.setPrefSize(BOARD_WIDTH * CELL_SIZE + 25, BOARD_HEIGHT * CELL_SIZE + 25);
        boardChessPane.setStyle("-fx-background-color: orange;");
        pieces = new Circle[BOARD_WIDTH][BOARD_HEIGHT];

        // Vẽ bàn cờ
        drawBoardChess();
        // Đặt quân cờ
        setupInitialPieces(chessColor);

        // chat part
        VBox chatBox = new VBox(10);
        chatBox.setAlignment(Pos.CENTER);
        chatBox.setPadding(new Insets(20));

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setFocusTraversable(false);
        chatArea.setPrefSize(250, 400);

        Text playerName = new Text(network.getPlayerInfo().username);
        Text chatText = new Text("Chat Area");
        TextField messageField = new TextField();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(e -> {
            if(!messageField.getText().isEmpty()) {
                network.sendChat(messageField.getText());
            }
            messageField.clear();
        });

        chatBox.getChildren().addAll(playerName, chatText, chatArea, messageField, sendButton);

        // root scene
        root.getChildren().addAll(boardChessPane, chatBox);
        Scene scene = new Scene(root, BOARD_WIDTH * CELL_SIZE + 300, BOARD_HEIGHT * CELL_SIZE + 25);
        stage.setScene(scene);
    }

    public static void onChatReceived(ChatMessage chat) {
        Platform.runLater(() -> chatArea.appendText(chat.sender + ": " + chat.message + "\n"));
    }

    public static void drawBoardChess() {
        Platform.runLater(() -> {
            // Vẽ lưới
            for (int i = 0; i < BOARD_WIDTH - 1; i++) {
                for (int j = 0; j < BOARD_HEIGHT - 1; j++) {
                    Line hLine = new Line(i * CELL_SIZE + 25, j * CELL_SIZE + 25, (i + 1) * CELL_SIZE + 25, j * CELL_SIZE + 25);
                    Line vLine = new Line(i * CELL_SIZE + 25, j * CELL_SIZE + 25, i * CELL_SIZE + 25, (j + 1) * CELL_SIZE + 25);
                    hLine.setStroke(Color.BLACK);
                    vLine.setStroke(Color.BLACK);
                    boardChessPane.getChildren().add(hLine);
                    if (i != 0 && i <BOARD_WIDTH - 1 && j == 4) continue;
                    boardChessPane.getChildren().add(vLine);
                }
            }
            for (int i = 0 ; i < BOARD_WIDTH - 1; i++) {
                Line hLine = new Line(i * CELL_SIZE + 25, (BOARD_HEIGHT - 1) * CELL_SIZE + 25, (i + 1) * CELL_SIZE + 25, (BOARD_HEIGHT - 1) * CELL_SIZE + 25);
                hLine.setStroke(Color.BLACK);
                boardChessPane.getChildren().addAll(hLine);
            }
            for (int j = 0 ; j < BOARD_HEIGHT - 1; j++) {
                Line vLine = new Line((BOARD_WIDTH - 1) * CELL_SIZE + 25, j * CELL_SIZE + 25, (BOARD_WIDTH - 1) * CELL_SIZE + 25, (j + 1) * CELL_SIZE + 25);
                vLine.setStroke(Color.BLACK);
                boardChessPane.getChildren().addAll(vLine);
            }

            // Vẽ cung Tướng (diagonal lines trong khu vực 3x3 của Tướng)
            Line diag1 = new Line(3 * CELL_SIZE + 25, 0 * CELL_SIZE + 25, 5 * CELL_SIZE + 25, 2 * CELL_SIZE + 25);
            Line diag2 = new Line(5 * CELL_SIZE + 25, 0 * CELL_SIZE + 25, 3 * CELL_SIZE + 25, 2 * CELL_SIZE + 25);
            Line diag3 = new Line(3 * CELL_SIZE + 25, 7 * CELL_SIZE + 25, 5 * CELL_SIZE + 25, 9 * CELL_SIZE + 25);
            Line diag4 = new Line(5 * CELL_SIZE + 25, 7 * CELL_SIZE + 25, 3 * CELL_SIZE + 25, 9 * CELL_SIZE + 25);
            diag1.setStroke(Color.BLACK);
            diag2.setStroke(Color.BLACK);
            diag3.setStroke(Color.BLACK);
            diag4.setStroke(Color.BLACK);
            boardChessPane.getChildren().addAll(diag1, diag2, diag3, diag4);

            // Vẽ sông (khoảng trống giữa hàng 4 và 5)
            Line river = new Line(25, 4.5 * CELL_SIZE + 25, (BOARD_WIDTH - 1) * CELL_SIZE + 25, 4.5 * CELL_SIZE + 25);
            river.setStroke(Color.BLUE);
            boardChessPane.getChildren().add(river);
        });
    }

    public static void setupInitialPieces(String chessColor) {
        Platform.runLater(() -> {
            initialBoard = new String[][]{
                    {"Xe", "Mã", "Tượng", "Sĩ", "Tướng", "Sĩ", "Tượng", "Mã", "Xe"},
                    {"", "", "", "", "", "", "", "", ""},
                    {"", "Pháo", "", "", "", "", "", "Pháo", ""},
                    {"Tốt", "", "Tốt", "", "Tốt", "", "Tốt", "", "Tốt"},
                    {"", "", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", ""},
                    {"Tốt", "", "Tốt", "", "Tốt", "", "Tốt", "", "Tốt"},
                    {"", "Pháo", "", "", "", "", "", "Pháo", ""},
                    {"", "", "", "", "", "", "", "", ""},
                    {"Xe", "Mã", "Tượng", "Sĩ", "Tướng", "Sĩ", "Tượng", "Mã", "Xe"}
            };

            String opponentColor = (chessColor.equals("red") ? "black" : "red");
            for (int x = 0; x < BOARD_WIDTH; x++) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    if (!initialBoard[y][x].isEmpty()) {
                        String color = (y < 5 ? opponentColor : chessColor);
                        Image image = getPieceImage(initialBoard[y][x], color);
                        Circle piece = new Circle(x * CELL_SIZE + 25, y * CELL_SIZE + 25, 20);
                        piece.setFill(new ImagePattern(image));
                        if (color.equals(chessColor)) { piece.setUserData(true);}
                        if (y > 4) {
                            piece.setOnMouseClicked(e -> {
                                if (choosenPiece != null) choosenPiece.setStroke(null);
                                piece.setStroke(Color.WHITE);
                                choosenPiece = piece;
                                setMovement();
                            });
                        }
                        pieces[x][y] = piece;
                        boardChessPane.getChildren().addAll(piece);
                    }
                }
            }
        });
    }

    public static void setMovement() {
        if (!yourTurn) return;
        int X = (int) ((choosenPiece.getCenterX() - 25) / CELL_SIZE);
        int Y = (int) ((choosenPiece.getCenterY() - 25) / CELL_SIZE);
        removeMovement();
        movementPieces = checkMovement(initialBoard[Y][X], X, Y, pieces);
        for (int i = 0; i < movementPieces.size(); i++) {
            Circle goalPiece = movementPieces.get(i);
            movementPieces.get(i).setOnMouseClicked(e -> {
                int toX = (int) ((goalPiece.getCenterX() - 25) / CELL_SIZE);
                int toY = (int) ((goalPiece.getCenterY() - 25) / CELL_SIZE);
                // di chuyển quân cờ
                choosenPiece.setCenterX(toX * CELL_SIZE + 25);
                choosenPiece.setCenterY(toY * CELL_SIZE + 25);
                boardChessPane.getChildren().remove(pieces[toX][toY]);
                pieces[toX][toY] = choosenPiece;
                pieces[X][Y] = null;
                if (initialBoard[toY][toX].equals("Tướng")) endGame(true);
                initialBoard[toY][toX] = initialBoard[Y][X];
                initialBoard[Y][X] = "";
                network.requestMovement(X, toX, Y, toY);
                removeMovement();
                yourTurn = false;
            });
            boardChessPane.getChildren().add(goalPiece);
        }
    }

    public static void removeMovement() {
        if (movementPieces != null) {
            boardChessPane.getChildren().removeAll(movementPieces);
        }
        movementPieces.clear();
    }

    public static void handleMovement(int X, int Y, int toX, int toY) {
        Platform.runLater(() -> {
            // di chuyển quân cờ
            Circle piece = pieces[X][Y];
            piece.setCenterX(toX * CELL_SIZE + 25);
            piece.setCenterY(toY * CELL_SIZE + 25);
            boardChessPane.getChildren().remove(pieces[toX][toY]);
            pieces[toX][toY] = piece;
            pieces[X][Y] = null;
            if (initialBoard[toY][toX].equals("Tướng")) endGame(false);
            initialBoard[toY][toX] = initialBoard[Y][X];
            initialBoard[Y][X] = "";
            yourTurn = true;
        });
    }

    public static void endGame(boolean winner) {
        Platform.runLater(() -> {
            network.leaveRoom();
            RoomListScene.show(stage, network);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            if (winner) alert.setContentText("Congratulations!");
            else alert.setContentText("Have a better game!");
            alert.showAndWait();
        });
    }

    public static Image getPieceImage(String pieceName, String color) {
        String imagePath = new String();
        if (pieceName.equals("Xe")) {
            if(color.equals("red")) { imagePath = "/XeDo.png"; }
            else if(color.equals("black")) { imagePath = "/XeDen.png"; }
        } else if (pieceName.equals("Mã")) {
            if(color.equals("red")) { imagePath = "/MaDo.png"; }
            else if(color.equals("black")) { imagePath = "/MaDen.png"; }
        } else if (pieceName.equals("Tượng")) {
            if(color.equals("red")) { imagePath = "/TuongjDo.png"; }
            else if(color.equals("black")) { imagePath = "/TuongjDen.png"; }
        } else if (pieceName.equals("Sĩ")) {
            if(color.equals("red")) { imagePath = "/SiDo.png"; }
            else if(color.equals("black")) { imagePath = "/SiDen.png"; }
        } else if (pieceName.equals("Tướng")) {
            if(color.equals("red")) { imagePath = "/TuongDo.png"; }
            else if(color.equals("black")) { imagePath = "/TuongDen.png"; }
        } else if (pieceName.equals("Pháo")) {
            if(color.equals("red")) { imagePath = "/PhaoDo.png"; }
            else if(color.equals("black")) { imagePath = "/PhaoDen.png"; }
        } else if (pieceName.equals("Tốt")) {
            if(color.equals("red")) { imagePath = "/TotDo.png"; }
            else if(color.equals("black")) { imagePath = "/TotDen.png"; }
        }
        Image image = new Image(GamePlayScene.class.getResourceAsStream(imagePath));
        return image;
    }
}