package common;

import java.util.ArrayList;
import java.util.List;

public class Network {
    // Yêu cầu đăng nhập
    public static class LoginRequest {
        public String username;
    }

    // Phản hồi đăng nhập
    public static class LoginResponse {
        public boolean success;
        public String message;
        public PlayerInfo playerInfo;
    }

    // Thông tin người chơi
    public static class PlayerInfo {
        public int id;
        public String username;
        public boolean isReady;
    }

    // Yêu cầu tạo phòng
    public static class CreateRoomRequest {}

    // Phản hồi tạo phòng
    public static class RoomCreatedResponse {
        public String roomId;
    }

    // Yêu cầu tham gia phòng
    public static class JoinRoomRequest {
        public String roomId;
    }

    public static class ViewerRequest {
        public String roomId;
    }

    public static class ViewerResponse {
        public boolean gameStarted;
        public String[][] initialBoard;
    }

    public static class ViewerUpdate {
        public int frX;
        public int toX;
        public int frY;
        public int toY;
    }

    // Yêu cầu danh sách người chơi
    public static class PlayerListRequest {}

    // Yêu cầu danh sách phòng
    public static class RoomListRequest {}

    // Yêu cầu thoát phòng
    public static class LeaveRoomRequest {}

    public static class LeaveViewRequest {}

    // Danh sách người chơi
    public static class PlayerListResponse {
        public List<PlayerInfo> players = new ArrayList<>();
    }

    // Danh sách phòng
    public static class RoomListResponse {
        public List<RoomInfo> rooms = new ArrayList<>();
    }

    // Yêu cầu sẵn sàng chơi
    public static class ReadyRequest {
        public boolean isReady;
    }

    public static class ReadyResponse {
        public boolean startGame;
        public String chessColor;
    }

    public static class MovementRequest {
        public int frX;
        public int toX;
        public int frY;
        public int toY;
    }

    public static class MovementResponse {
        public int frX;
        public int toX;
        public int frY;
        public int toY;
    }

    // Thông tin phòng
    public static class RoomInfo {
        public String roomId;
        public List<PlayerInfo> players = new ArrayList<>();
        public boolean gameStarted;
        public String[][] board;
        public List<PlayerInfo> viewers = new ArrayList<>();
    }

    // Cập nhật phòng đợi
    public static class RoomUpdate {
        public List<PlayerInfo> players = new ArrayList<>();
        public boolean canStart; // True nếu đủ 2 người
    }

    // Tin nhắn chat
    public static class ChatMessage {
        public String sender;
        public String message;
    }
}