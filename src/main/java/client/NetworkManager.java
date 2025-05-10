package client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import common.Network.*;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

public class NetworkManager {
    private Client client;
    private PlayerInfo playerInfo;
    private RoomUpdate currentRoom;
    private String currentRoomId;
    private boolean isJoiningFromList = false;

    public NetworkManager() throws IOException {
        client = new Client();
        registerClasses();
        client.start();
        client.connect(5000, "localhost", 54555, 54777);

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof LoginResponse) {
                    handleLoginResponse((LoginResponse) object);
                } else if (object instanceof RoomCreatedResponse) {
                    handleRoomCreated((RoomCreatedResponse) object);
                } else if (object instanceof RoomUpdate) {
                    handleRoomUpdate((RoomUpdate) object);
                } else if (object instanceof ChatMessage) {
                    handleChat((ChatMessage) object);
                } else if (object instanceof RoomListResponse) {
                    handleRoomListResponse((RoomListResponse) object);
                } else if (object instanceof PlayerListResponse) {
                    handlePlayerListResponse((PlayerListResponse) object);
                } else if (object instanceof ReadyResponse) {
                    handleReadyResponse((ReadyResponse) object);
                } else if (object instanceof MovementResponse) {
                    handleMovementResponse((MovementResponse) object);
                } else if (object instanceof ViewerResponse) {
                    handleViewerResponse((ViewerResponse) object);
                } else if (object instanceof ViewerUpdate) {
                    handleViewerUpdate((ViewerUpdate) object);
                }
            }
        });
    }

    private void registerClasses() {
        client.getKryo().register(LoginRequest.class);
        client.getKryo().register(LoginResponse.class);
        client.getKryo().register(PlayerInfo.class);
        client.getKryo().register(CreateRoomRequest.class);
        client.getKryo().register(RoomCreatedResponse.class);
        client.getKryo().register(JoinRoomRequest.class);
        client.getKryo().register(PlayerListRequest.class);
        client.getKryo().register(PlayerListResponse.class);
        client.getKryo().register(RoomListRequest.class);
        client.getKryo().register(RoomListResponse.class);
        client.getKryo().register(RoomInfo.class);
        client.getKryo().register(RoomUpdate.class);
        client.getKryo().register(ReadyRequest.class);
        client.getKryo().register(ReadyResponse.class);
        client.getKryo().register(ChatMessage.class);
        client.getKryo().register(LeaveRoomRequest.class);
        client.getKryo().register(ArrayList.class);
        client.getKryo().register(MovementRequest.class);
        client.getKryo().register(MovementResponse.class);
        client.getKryo().register(ViewerRequest.class);
        client.getKryo().register(ViewerResponse.class);
        client.getKryo().register(ViewerUpdate.class);
        client.getKryo().register(LeaveViewRequest.class);
        client.getKryo().register(String[][].class);
        client.getKryo().register(String[].class);
    }

    public void sendLogin(String username) {
        LoginRequest login = new LoginRequest();
        login.username = username;
        client.sendTCP(login);
    }

    public void createRoom() {
        isJoiningFromList = false;
        client.sendTCP(new CreateRoomRequest());
    }

    public void joinRoom(String roomId) {
        isJoiningFromList = true;
        currentRoomId = roomId;
        JoinRoomRequest join = new JoinRoomRequest();
        join.roomId = roomId;
        client.sendTCP(join);
    }

    public void viewRoom(String roomId) {
        ViewerRequest viewer = new ViewerRequest();
        viewer.roomId = roomId;
        client.sendTCP(viewer);
    }

    public void sendChat(String message) {
        ChatMessage chat = new ChatMessage();
        chat.sender = playerInfo.username;
        chat.message = message;
        client.sendTCP(chat);
    }

    public void leaveRoom() {
        client.sendTCP(new LeaveRoomRequest());
        currentRoom = null;
        currentRoomId = null;
    }

    public void leaveView() {
        client.sendTCP(new LeaveViewRequest());
    }

    public void requestRoomList() {
        client.sendTCP(new RoomListRequest());
    }

    public void requestPlayerList() {
        client.sendTCP(new PlayerListRequest());
    }

    public void requestReady(boolean isReady) {
        ReadyRequest ready = new ReadyRequest();
        ready.isReady = isReady;
        client.sendTCP(ready);
    }

    public void requestMovement(int frX, int toX, int frY, int toY) {
        MovementRequest move = new MovementRequest();
        move.frX = frX;
        move.frY = frY;
        move.toX = toX;
        move.toY = toY;
        client.sendTCP(move);
    }

    private void handleLoginResponse(LoginResponse response) {
        if (response.success) {
            playerInfo = response.playerInfo;
            LoginScene.onLoginSuccess();

        }
    }

    private void handleRoomCreated(RoomCreatedResponse response) {
        currentRoomId = response.roomId;
        RoomListScene.onRoomCreated(response.roomId);
    }

    private void handleRoomUpdate(RoomUpdate update) {
        currentRoom = update;
        RoomWaitingScene.onRoomUpdate(update);
        if (isJoiningFromList) {
            RoomListScene.onJoinSuccess();
        }
    }

    private void handleReadyResponse(ReadyResponse response) {
        if (response.startGame) RoomWaitingScene.startGame(response.chessColor);
    }

    private void handleMovementResponse(MovementResponse response) {
        GamePlayScene.handleMovement(response.frX, response.frY, response.toX, response.toY);
    }

    private void handleViewerResponse(ViewerResponse response) {
        RoomListScene.onViewRoom(response);
    }

    private void handleViewerUpdate(ViewerUpdate update) {
        GameViewScene.updateView(update);
    }

    private void handleChat(ChatMessage chat) {
        GamePlayScene.onChatReceived(chat);
    }

    private void handleRoomListResponse(RoomListResponse response) {
        RoomListScene.onRoomListReceived(response); // Nhận bất kỳ lúc nào từ server
    }

    private void handlePlayerListResponse(PlayerListResponse response) {
        RoomListScene.onPlayerListReceived(response); // Nhận bất kỳ lúc nào t server
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public RoomUpdate getCurrentRoom() {
        return currentRoom;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }
}