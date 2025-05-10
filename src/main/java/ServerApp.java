import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import common.Network.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ServerApp {
    private Server server;
    private HashMap<String, RoomInfo> rooms = new HashMap<>();
    private HashMap<Connection, PlayerInfo> players = new HashMap<>();

    public ServerApp() throws IOException {
        server = new Server();
        registerClasses();

        server.start();
        server.bind(54555, 54777); // TCP 54555, UDP 54777

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("Client connected: " + connection.getID());
            }

            @Override
            public void disconnected(Connection connection) {
                PlayerInfo player = players.remove(connection);
                if (player != null) {
                    removePlayerFromRoom(player, connection);
                    broadcastRoomList();
                    broadcastPlayerList();
                }
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof LoginRequest) {
                    handleLogin(connection, (LoginRequest) object);
                } else if (object instanceof CreateRoomRequest) {
                    handleCreateRoom(connection);
                } else if (object instanceof JoinRoomRequest) {
                    handleJoinRoom(connection, (JoinRoomRequest) object);
                } else if (object instanceof ChatMessage) {
                    handleChat(connection, (ChatMessage) object);
                } else if (object instanceof LeaveRoomRequest) {
                    handleLeaveRoom(connection);
                } else if (object instanceof RoomListRequest) {
                    handleRoomListRequest(connection);
                } else if (object instanceof PlayerListRequest) {
                    handlePlayerListRequest(connection);
                } else if (object instanceof ReadyRequest) {
                    handleReadyRequest(connection, (ReadyRequest) object);
                } else if (object instanceof MovementRequest) {
                    handleMovementRequest(connection, (MovementRequest) object);
                } else if (object instanceof ViewerRequest) {
                    handleViewerRequest(connection, (ViewerRequest) object);
                } else if (object instanceof LeaveViewRequest) {
                    handleLeaveView(connection);
                }
            }
        });
    }

    private void registerClasses() {
        server.getKryo().register(LoginRequest.class);
        server.getKryo().register(LoginResponse.class);
        server.getKryo().register(PlayerInfo.class);
        server.getKryo().register(CreateRoomRequest.class);
        server.getKryo().register(RoomCreatedResponse.class);
        server.getKryo().register(JoinRoomRequest.class);
        server.getKryo().register(PlayerListRequest.class);
        server.getKryo().register(PlayerListResponse.class);
        server.getKryo().register(RoomListRequest.class);
        server.getKryo().register(RoomListResponse.class);
        server.getKryo().register(RoomInfo.class);
        server.getKryo().register(RoomUpdate.class);
        server.getKryo().register(ReadyRequest.class);
        server.getKryo().register(ReadyResponse.class);
        server.getKryo().register(ChatMessage.class);
        server.getKryo().register(LeaveRoomRequest.class);
        server.getKryo().register(ArrayList.class);
        server.getKryo().register(MovementRequest.class);
        server.getKryo().register(MovementResponse.class);
        server.getKryo().register(ViewerRequest.class);
        server.getKryo().register(ViewerResponse.class);
        server.getKryo().register(ViewerUpdate.class);
        server.getKryo().register(LeaveViewRequest.class);
        server.getKryo().register(String[][].class);
        server.getKryo().register(String[].class);
    }

    private void handleLogin(Connection connection, LoginRequest request) {
        PlayerInfo player = new PlayerInfo();
        player.id = connection.getID();
        player.username = request.username;
        player.isReady = false;
        players.put(connection, player);
        broadcastPlayerList();

        LoginResponse response = new LoginResponse();
        response.success = true;
        response.message = "Logged in successfully";
        response.playerInfo = player;
        connection.sendTCP(response);
    }

    private void handleCreateRoom(Connection connection) {
        String roomId = String.format("%04d", new Random().nextInt(10000));
        RoomInfo room = new RoomInfo();
        room.roomId = roomId;
        room.players.add(players.get(connection));
        rooms.put(roomId, room);

        RoomCreatedResponse response = new RoomCreatedResponse();
        response.roomId = roomId;
        connection.sendTCP(response);

        updateRoom(connection, room);
        broadcastRoomList();
    }

    private void handleJoinRoom(Connection connection, JoinRoomRequest request) {
        RoomInfo room = rooms.get(request.roomId);
        if (room != null && room.players.size() < 2) {
            room.players.add(players.get(connection));
            updateRoom(connection, room);
            for (Connection c : server.getConnections()) {
                if (c != connection && room.players.contains(players.get(c))) {
                    updateRoom(c, room);
                }
            }
            broadcastRoomList();
        }
    }

    private void handleLeaveRoom(Connection connection) {
        PlayerInfo player = players.get(connection);
        if (player != null) {
            removePlayerFromRoom(player, connection);
            broadcastRoomList();
        }
    }

    private void handleLeaveView(Connection connection) {
        PlayerInfo player = players.get(connection);
        RoomInfo room = findRoomByViewer(player);
        if (player != null && room != null) {
            room.viewers.remove(player);
        }
    }

    private void handleChat(Connection connection, ChatMessage chat) {
        RoomInfo room = findRoomByPlayer(players.get(connection));
        if (room != null) {
            for (Connection c : server.getConnections()) {
                if (room.players.contains(players.get(c))) {
                    c.sendTCP(chat);
                }
            }
        }
    }

    private void handlePlayerListRequest(Connection connection) {
        PlayerListResponse response = new PlayerListResponse();
        response.players.addAll(players.values());
        connection.sendTCP(response);
    }

    private void handleRoomListRequest(Connection connection) {
        RoomListResponse response = new RoomListResponse();
        response.rooms.addAll(rooms.values()); // Thêm tất cả phòng hiện có
        connection.sendTCP(response);
    }

    private void updateRoom(Connection connection, RoomInfo room) {
        RoomUpdate update = new RoomUpdate();
        update.players = room.players;
        update.canStart = room.players.size() == 2;
        connection.sendTCP(update);
    }

    private  void handleReadyRequest(Connection connection, ReadyRequest request) {
        ReadyResponse response = new ReadyResponse();
        response.startGame = true;
        PlayerInfo player = players.get(connection);
        RoomInfo room = findRoomByPlayer(player);

        if (player != null) { player.isReady = request.isReady; }
        if (room != null) {
            for (Connection c : server.getConnections()) {
                if (room.players.contains(players.get(c))) {
                    response.startGame = (response.startGame && players.get(c).isReady);
                    room.gameStarted = response.startGame;
                    if (room.gameStarted) {
                        String[][] initialBoard = new String[][]{
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
                        room.board = initialBoard;
                    }
                }
            }
            for (Connection c : server.getConnections()) {
                if (room.players.contains(players.get(c))) {
                    response.chessColor = (players.get(c).equals(room.players.get(0)) ? "red" : "black");
                    c.sendTCP(response);
                }
            }
        }
    }

    private void handleMovementRequest(Connection connection, MovementRequest request) {
        PlayerInfo player = players.get(connection);
        RoomInfo room = findRoomByPlayer(player);

        MovementResponse response = new MovementResponse();
        response.frX = 8 - request.frX;
        response.frY = 9 - request.frY;
        response.toX = 8 - request.toX;
        response.toY = 9 - request.toY;

        String chessColor = (player.equals(room.players.get(0)) ? "red" : "black");
        ViewerUpdate update = new ViewerUpdate();
        update.frX = chessColor.equals("red") ? request.frX : response.frX;
        update.frY = chessColor.equals("red") ? request.frY : response.frY;
        update.toX = chessColor.equals("red") ? request.toX : response.toX;
        update.toY = chessColor.equals("red") ? request.toY : response.toY;

        //edit board
        room.board[update.toY][update.toX] = room.board[update.frY][update.frX];
        room.board[update.frY][update.frX] = "";

        if (room != null) {
            for (Connection c : server.getConnections()) {
                if (room.players.contains(players.get(c)) && players.get(c).equals(player) == false) {
                    c.sendTCP(response);
                }
                if (room.viewers.contains(players.get(c))) {
                    c.sendTCP(update);
                }
            }
        }
    }

    private void handleViewerRequest(Connection connection, ViewerRequest request) {
        RoomInfo room = rooms.get(request.roomId);
        ViewerResponse response = new ViewerResponse();
        response.gameStarted = room.gameStarted;
        if (response.gameStarted) {
            room.viewers.add(players.get(connection));
            response.initialBoard = room.board;
        }
        connection.sendTCP(response);
    }

    private RoomInfo findRoomByPlayer(PlayerInfo player) {
        for (RoomInfo room : rooms.values()) {
            if (room.players.contains(player)) return room;
        }
        return null;
    }

    private RoomInfo findRoomByViewer(PlayerInfo viewer) {
        for (RoomInfo room : rooms.values()) {
            if (room.viewers.contains(viewer)) return room;
        }
        return null;
    }

    private void removePlayerFromRoom(PlayerInfo player, Connection connection) {
        RoomInfo room = findRoomByPlayer(player);
        if (room != null) {
            room.players.remove(player);
            if (room.players.isEmpty()) {
                rooms.remove(room.roomId);
            } else {
                for (Connection c : server.getConnections()) {
                    if (room.players.contains(players.get(c))) {
                        updateRoom(c, room);
                    }
                }
            }
        }
    }

    private void broadcastPlayerList() {
        PlayerListResponse response = new PlayerListResponse();
        response.players.addAll(players.values());
        for (Connection c : server.getConnections()) {
            c.sendTCP(response); // Gửi tới tất cả client
        }
    }

    private void broadcastRoomList() {
        RoomListResponse response = new RoomListResponse();
        response.rooms.addAll(rooms.values());
        for (Connection c : server.getConnections()) {
            c.sendTCP(response); // Gửi tới tất cả client
        }
    }

    public static void main(String[] args) throws IOException {
        new ServerApp();
        System.out.println("Server started.");
    }
}