package server;

import models.ClientModel;
import utils.Message;
import utils.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class PlayerHandler implements Runnable{
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    public ClientModel player;
    public Game game = null;

    public PlayerHandler(Socket socket) throws IOException {
        this.socket = socket;
        initializeInputOutputStreams(socket);
    }

    @Override
    public void run() {
        try {
            if (authenticate()) {
                addToQueue();
                playGame();
            }
        }
        catch (IOException e) {
            System.out.println("Error in PlayerHandler: " + e);
            closeAllResource();
        }
    }

    private void addToQueue() {
        Server.addToQueue(this);
        write(new Message(MessageType.MESSAGE, "JOIN_QUEUE", "Server"));
    }

    private void playGame() {
        while (true){
            try {
                Message message = (Message) inputStream.readObject();

                switch (message.getMessageType()) {
                    case MESSAGE:
                        if (game != null){
                            System.out.println("[Game" + game.gameNumber + "] " + player.username + "> " + message.getMessageBody());
                            broadcastMessage(message);
                        }
                        break;
                    case DISCONNECT:
                        System.out.println("Client " + player.username + " has disconnected");
                        game.removePlayer(this);
                        if (game.players.size() == 0){
                            game.close();
                        }
                        game = null;
                        closeAllResource();
                        return;
                }
            } catch (Exception e) {
                System.out.println("Client " + player.username + " lost connection");
                closeAllResource();
                break;
            }
        }
    }

    private boolean authenticate() throws IOException{
        boolean isValid = false;
        try {
            Authentication authentication = new Authentication();
            Message message = (Message) inputStream.readObject();

            switch (message.getMessageType()) {
                case LOGIN:
                    this.player = authentication.login(message);
                    if (this.player == null){
                        write(new Message(MessageType.LOGIN, "UNSUCCESSFUL", "server"));
                        System.out.println("Failed Connection: " + socket);
                    }
                    else {
                        write(new Message(MessageType.LOGIN, "SUCCESSFUL", "server"));
                        System.out.println("Client " + player.username + " has connected");
                        isValid = true;
                    }
                    break;
                case REGISTER:
                    System.out.println("Register");
                    break;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return isValid;
    }

    public boolean write(Message message) {
        if (!socket.isConnected()) {
            closeAllResource();
            return false;
        }

        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            // if an error occurs, do not abort just inform the user
            System.out.println("Error sending message to " + player.username);
        }
        return true;
    }

    public Message read() {
        Message message;
        try {
            message = (Message) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    public void closeAllResource() {
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.println("Error closing resources: " + e);
        }
    }

    private void initializeInputOutputStreams(Socket socket) throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    private void broadcastMessage(Message message) {
        for (PlayerHandler playerHandler : game.players) {
            if (playerHandler == this) continue;
            playerHandler.write(message);
        }
    }
}
