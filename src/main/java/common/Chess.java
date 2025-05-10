package common;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Chess {
    private static final int BOARD_WIDTH = 9;  // 9 cột
    private static final int BOARD_HEIGHT = 10; // 10 hàng
    private static final double CELL_SIZE = 50;

    public static ArrayList<Circle> checkMovement(String chessName, int X, int Y, Circle[][] pieces) {
        ArrayList<Circle> movementPieces = new ArrayList<>();

        if (chessName.equals("Tốt")) {
            System.out.println(pieces[X][Y - 1] + " " + isTeammate(pieces[X][Y - 1]));
            if (Y > 0 && !isTeammate(pieces[X][Y - 1])) {
                Circle piece = createCircle(X, Y - 1, 10);
                movementPieces.add(piece);
            }
            if (Y <= 4) {
                if (X > 0 && !isTeammate(pieces[X - 1][Y])) {
                    Circle piece = createCircle(X - 1, Y, 10);
                    movementPieces.add(piece);
                }
                if (X < 8 && !isTeammate(pieces[X + 1][Y])) {
                    Circle piece = createCircle(X + 1, Y, 10);
                    movementPieces.add(piece);
                }
            }
            return movementPieces;
        } else if (chessName.equals("Pháo")) {
            int x = X - 1; int y = Y; boolean mid = false;
            while (x >= 0) {
                if (!mid && pieces[x][y] == null) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                }
                if (mid && pieces[x][y] != null && !isTeammate(pieces[x][y])) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                    break;
                }
                if (!mid && pieces[x][y] != null) {
                    mid = true;
                }
                x = x - 1;
            }

            x = X + 1; mid = false;
            while (x < 9) {
                if (!mid && pieces[x][y] == null) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                }
                if (mid && pieces[x][y] != null && !isTeammate(pieces[x][y])) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                    break;
                }
                if (!mid && pieces[x][y] != null) {
                    mid = true;
                }
                x = x + 1;
            }

            x = X; y = Y - 1; mid = false;
            while (y >= 0) {
                if (!mid && pieces[x][y] == null) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                }
                if (mid && pieces[x][y] != null && !isTeammate(pieces[x][y])) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                    break;
                }
                if (!mid && pieces[x][y] != null) {
                    mid = true;
                }
                y = y - 1;
            }

            y = Y + 1; mid = false;
            while (y < 10) {
                if (!mid && pieces[x][y] == null) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                }
                if (mid && pieces[x][y] != null && !isTeammate(pieces[x][y])) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                    break;
                }
                if (!mid && pieces[x][y] != null) {
                    mid = true;
                }
                y = y + 1;
            }
        } else if (chessName.equals("Xe")) {
            int x = X - 1; int y = Y;
            while (x >= 0) {
                if (pieces[x][y] == null) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                } else {
                    if (!isTeammate(pieces[x][y])) {
                        Circle piece = createCircle(x, y, 10);
                        movementPieces.add(piece);
                    }
                    break;
                }
                x = x - 1;
            }

            x = X + 1;
            while (x < 9) {
                if (pieces[x][y] == null) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                } else {
                    if (!isTeammate(pieces[x][y])) {
                        Circle piece = createCircle(x, y, 10);
                        movementPieces.add(piece);
                    }
                    break;
                }
                x = x + 1;
            }

            x = X; y = Y - 1;
            while (y >= 0) {
                if (pieces[x][y] == null) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                } else {
                    if (!isTeammate(pieces[x][y])) {
                        Circle piece = createCircle(x, y, 10);
                        movementPieces.add(piece);
                    }
                    break;
                }
                y = y - 1;
            }

            y = Y + 1;
            while (y < 10) {
                if (pieces[x][y] == null) {
                    Circle piece = createCircle(x, y, 10);
                    movementPieces.add(piece);
                } else {
                    if (!isTeammate(pieces[x][y])) {
                        Circle piece = createCircle(x, y, 10);
                        movementPieces.add(piece);
                    }
                    break;
                }
                y = y + 1;
            }
        } else if (chessName.equals("Mã")) {
            if (X - 1 >= 0 && pieces[X - 1][Y] == null) {
                if (X - 2 >= 0 && Y - 1 >= 0 && !isTeammate(pieces[X - 2][Y - 1])) {
                    Circle piece = createCircle(X - 2, Y - 1, 10);
                    movementPieces.add(piece);
                }
                if (X - 2 >= 0 && Y + 1 < 10 && !isTeammate(pieces[X - 2][Y + 1])) {
                    Circle piece = createCircle(X - 2, Y + 1, 10);
                    movementPieces.add(piece);
                }
            }
            if (X + 1 < 9 && pieces[X + 1][Y] == null) {
                if (X + 2 < 9 && Y - 1 >= 0 && !isTeammate(pieces[X + 2][Y - 1])) {
                    Circle piece = createCircle(X + 2, Y - 1, 10);
                    movementPieces.add(piece);
                }
                if (X + 2 < 9 && Y + 1 < 10 && !isTeammate(pieces[X + 2][Y + 1])) {
                    Circle piece = createCircle(X + 2, Y + 1, 10);
                    movementPieces.add(piece);
                }
            }
            if (Y - 1 >= 0 && pieces[X][Y - 1] == null) {
                if (X - 1 >= 0 && Y - 2 >= 0 && !isTeammate(pieces[X - 1][Y - 2])) {
                    Circle piece = createCircle(X - 1, Y - 2, 10);
                    movementPieces.add(piece);
                }
                if (X + 1 < 9 && Y - 2 >= 0 && !isTeammate(pieces[X + 1][Y - 2])) {
                    Circle piece = createCircle(X + 1, Y - 2, 10);
                    movementPieces.add(piece);
                }
            }
            if (Y + 1 < 10 && pieces[X][Y + 1] == null) {
                if (X - 1 >= 0 && Y + 2 >= 0 && !isTeammate(pieces[X - 1][Y + 2])) {
                    Circle piece = createCircle(X - 1, Y + 2, 10);
                    movementPieces.add(piece);
                }
                if (X + 1 < 9 && Y + 2 >= 0 && !isTeammate(pieces[X + 1][Y + 2])) {
                    Circle piece = createCircle(X + 1, Y + 2, 10);
                    movementPieces.add(piece);
                }
            }
        } else if (chessName.equals("Tượng")) {
            if (X - 2 >= 0 && Y - 2 >= 5 && pieces[X - 1][Y - 1] == null && !isTeammate(pieces[X - 2][Y - 2])) {
                Circle piece = createCircle(X - 2, Y - 2, 10);
                movementPieces.add(piece);
            }
            if (X - 2 >= 0 && Y + 2 < 10 && pieces[X - 1][Y + 1] == null && !isTeammate(pieces[X - 2][Y + 2])) {
                Circle piece = createCircle(X - 2, Y + 2, 10);
                movementPieces.add(piece);
            }
            if (X + 2 <= 9 && Y - 2 >= 5 && pieces[X + 1][Y - 1] == null && !isTeammate(pieces[X + 2][Y - 2])) {
                Circle piece = createCircle(X + 2, Y - 2, 10);
                movementPieces.add(piece);
            }
            if (X + 2 <= 9 && Y + 2 <= 10 && pieces[X + 1][Y + 1] == null && !isTeammate(pieces[X + 2][Y + 2])) {
                Circle piece = createCircle(X + 2, Y + 2, 10);
                movementPieces.add(piece);
            }
        } else if (chessName.equals("Sĩ")) {
            if (X - 1 >= 3 && Y - 1 >= 7 && !isTeammate(pieces[X - 1][Y - 1])) {
                Circle piece = createCircle(X - 1, Y - 1, 10);
                movementPieces.add(piece);
            }
            if (X - 1 >= 3 && Y + 1 < 10 && !isTeammate(pieces[X - 1][Y + 1])) {
                Circle piece = createCircle(X - 1, Y + 1, 10);
                movementPieces.add(piece);
            }
            if (X + 1 <= 5 && Y - 1 >= 7 && !isTeammate(pieces[X + 1][Y - 1])) {
                Circle piece = createCircle(X + 1, Y - 1, 10);
                movementPieces.add(piece);
            }
            if (X + 1  <= 5 && Y + 1 < 10 && !isTeammate(pieces[X + 1][Y + 1])) {
                Circle piece = createCircle(X + 1, Y + 1, 10);
                movementPieces.add(piece);
            }
        } else if (chessName.equals("Tướng")) {
            if (X - 1 >= 3 && !isTeammate(pieces[X - 1][Y])) {
                Circle piece = createCircle(X - 1, Y, 10);
                movementPieces.add(piece);
            }
            if (X + 1 <= 5 && !isTeammate(pieces[X + 1][Y])) {
                Circle piece = createCircle(X + 1, Y, 10);
                movementPieces.add(piece);
            }
            if (Y - 1 >= 7 && !isTeammate(pieces[X][Y - 1])) {
                Circle piece = createCircle(X, Y - 1, 10);
                movementPieces.add(piece);
            }
            if (Y + 1 < 10 && !isTeammate(pieces[X][Y + 1])) {
                Circle piece = createCircle(X, Y + 1, 10);
                movementPieces.add(piece);
            }
        }

        return movementPieces;
    }

    public static boolean isTeammate(Circle circle) {
        if (circle == null) return false;
        else if (circle.getUserData() == null) return false;
        return true;
    }

    public static Circle createCircle(int x, int y, int radius) {
        Circle circle = new Circle(x * CELL_SIZE + 25, y * CELL_SIZE + 25, radius);
        circle.setFill(Color.RED);
        circle.setStroke(Color.RED);
        return circle;
    }
}
