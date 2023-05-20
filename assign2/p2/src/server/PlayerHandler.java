package server;

import models.ClientModel;
import utils.Message;
import utils.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class PlayerHandler implements Runnable{
    public Socket socket;
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
    }

    private void playGame() {
        while (true){
            try {
                Message message = (Message) inputStream.readObject();

                switch (message.getMessageType()) {
                    case MESSAGE:
                        if (game != null){
                            switch (message.getMessageBody()) {
                                case ".gp":
                                    String playerList = "";
                                    for (PlayerHandler player : game.players) {
                                        playerList += player.player.username + ", ";
                                    }
                                    write(new Message(MessageType.MESSAGE, "Players: " + playerList, "Server"));
                                    break;
                                default:
                                    System.out.println("[" + Server.displayTime.format(new Date()) + "] " + "Game" + game.gameNumber + ": <" + player.username + "> " + message.getMessageBody());
                                    broadcastMessage(message);
                                    break;
                            }
                        }
                        break;
                    case DISCONNECT:
                        if (game != null) {
                            System.out.println("[" + Server.displayTime.format(new Date()) + "] " + player.username + " has disconnected");
                            game.removePlayer(this);
                            if (game.players.size() == 0) {
                                game.close();
                            }
                            game = null;
                            closeAllResource();
                        }
                        else {
                            System.out.println("[" + Server.displayTime.format(new Date()) + "] " + player.username + " has disconnected");
                            Server.removeFromQueue(this);
                            closeAllResource();
                        }
                        Server.serverDB.updateToken(player.username, "-");
                        return;
                }
            } catch (Exception e) {
                System.out.println("[" + Server.displayTime.format(new Date()) + "] " + player.username + " lost connection");
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
                        write(new Message(MessageType.LOGIN, "UNSUCCESSFUL", "Server"));
                        System.out.println("[" + Server.displayTime.format(new Date()) + "] " + "Failed Connection: " + socket);
                    }
                    else {
                        write(new Message(MessageType.LOGIN, "SUCCESSFUL", player.token));
                        if (authentication.isReconnect){
                            int index = 0;
                            boolean found = false;
                            for (Game g : Server.activeGames){
                                for (PlayerHandler p : g.players){
                                    if (p.player.username.equals(player.username)){
                                        g.removePlayer(p);
                                        game = g;
                                    }
                                }
                            }
                            if (game != null){
                                game.addPlayer(this);
                                found = true;
                                write(new Message(MessageType.MESSAGE, "You have rejoined the game", "Server"));
                                System.out.println("[" + Server.displayTime.format(new Date()) + "] " + player.username + " has reconnected and rejoined game " + game.gameNumber);
                            }
                            if (!found) {
                                for (int i = 0; i < Server.playerQueue.size(); i++) {
                                    if (Server.playerQueue.get(i).player.username.equals(player.username)) {
                                        Server.removeFromQueue(Server.playerQueue.get(i));
                                        index = i;
                                    }
                                }
                                Server.playerQueue.add(index, this);
                                write(new Message(MessageType.MESSAGE, "You have rejoined the queue", "Server"));
                                System.out.println("[" + Server.displayTime.format(new Date()) + "] " + player.username + " has reconnected and rejoined the queue");
                            }

                        }
                        else {
                            addToQueue();
                            write(new Message(MessageType.MESSAGE, "You have joined the queue", "Server"));
                            System.out.println("[" + Server.displayTime.format(new Date()) + "] " + player.username + " has connected and joined the queue");
                        }
                        isValid = true;
                    }
                    break;
                case REGISTER:
                    if (authentication.register(message)){
                        write(new Message(MessageType.REGISTER, "SUCCESSFUL", "-"));
                        System.out.println("[" + Server.displayTime.format(new Date()) + "] " + "New registry with username " + message.getMessageBody().split(" ")[0]);
                    }
                    else {
                        write(new Message(MessageType.REGISTER, "UNSUCCESSFUL", "Server"));
                        System.out.println("[" + Server.displayTime.format(new Date()) + "] " + "Failed to register new client: " + socket);
                    }
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

    public void setSocket(Socket socket) throws IOException{
        this.socket = socket;
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    private void broadcastMessage(Message message) {
        for (PlayerHandler playerHandler : game.players) {
            if (playerHandler == this) continue;
            playerHandler.write(message);
        }
    }
}
