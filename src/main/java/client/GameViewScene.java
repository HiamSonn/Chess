package client;

import common.Network;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class GameViewScene {
    private static Stage stage;
    private static NetworkManager network;
    private static Pane boardChessPane;

    private static final int BOARD_WIDTH = 9;  // 9 cột
    private static final int BOARD_HEIGHT = 10; // 10 hàng
    private static final double CELL_SIZE = 50;
    private static String[][] initialBoard;
    private static Circle[][] pieces;

    public static void show(Stage primaryStage,NetworkManager net, String[][] initial) {
        stage = primaryStage;
        network = net;
        initialBoard = initial;

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
        setupInitialPieces("red");

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            network.leaveView();
            Platform.runLater(() -> RoomListScene.show(primaryStage, network));
        });

        // root scene
        root.getChildren().addAll(boardChessPane, exitButton);
        Scene scene = new Scene(root, BOARD_WIDTH * CELL_SIZE + 100, BOARD_HEIGHT * CELL_SIZE + 25);
        stage.setScene(scene);
    }

    public static void updateView(Network.ViewerUpdate update) {
        Platform.runLater(() -> {
            int X = update.frX;
            int Y = update.frY;
            int toX = update.toX;
            int toY = update.toY;
            // di chuyển quân cờ
            Circle piece = pieces[X][Y];
            piece.setCenterX(toX * CELL_SIZE + 25);
            piece.setCenterY(toY * CELL_SIZE + 25);
            boardChessPane.getChildren().remove(pieces[toX][toY]);
            pieces[toX][toY] = piece;
            pieces[X][Y] = null;
            if (initialBoard[toY][toX].equals("Tướng")) endGame();
            initialBoard[toY][toX] = initialBoard[Y][X];
            initialBoard[Y][X] = "";
        });
    }

    public static void endGame() {
        Platform.runLater(() -> {
            network.leaveView();
            RoomListScene.show(stage, network);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("The game has ended!");
            alert.showAndWait();
        });
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
            String opponentColor = (chessColor.equals("red") ? "black" : "red");
            for (int x = 0; x < BOARD_WIDTH; x++) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    if (!initialBoard[y][x].isEmpty()) {
                        String color = (y < 5 ? opponentColor : chessColor);
                        Image image = getPieceImage(initialBoard[y][x], color);
                        Circle piece = new Circle(x * CELL_SIZE + 25, y * CELL_SIZE + 25, 20);
                        piece.setFill(new ImagePattern(image));
                        if (color.equals(chessColor)) { piece.setUserData(true);}
                        pieces[x][y] = piece;
                        boardChessPane.getChildren().addAll(piece);
                    }
                }
            }
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

