package client;

import models.ClientModel;
import utils.Message;
import utils.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerClient {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private String server;
    public ClientModel player;
    private int port;
    ClientServerListener serverListener;

    public PlayerClient(ConnectionInfo connectionInformation) {
        this.server = connectionInformation.getServerAddress();
        this.port = connectionInformation.getPortNumber();
        this.player = connectionInformation.client;
    }

    public boolean initialize() {

        try {
            initializeSocket();

            initializeInputOutputObjectStreams();

            serverListener = new ClientServerListener(player.username, inputStream, outputStream);
            serverListener.start();


        } catch (IOException e) {
            serverListener.setRunning(false);
            disconnect();
            return false;
        }

        return true;
    }

    public void sendMessage(Message msg) {
        try {
            outputStream.writeObject(msg);
        } catch (IOException e) {
            System.out.println("Exception writing to server: " + e);
        }
    }

    private void initializeSocket() throws IOException {
        try {
            socket = new Socket(server, port);

            System.out.println(String.format("Connection accepted %s : %s", socket.getInetAddress(), socket.getPort()));

        } catch (IOException e) {
            System.out.println("Error connecting to server:" + e);
        }
    }

    private void initializeInputOutputObjectStreams() throws IOException {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Exception creating new Input/output Streams: " + e);
        }
    }

    public void disconnect() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.println("Error closing the socket and streams" + e);
        }

    }

    public String getUsername() {
        return player.username;
    }


    public boolean processInputMessage(String userInputMessage) {
        if (userInputMessage.equalsIgnoreCase(".dis")) {
            sendMessage(new Message(MessageType.DISCONNECT, "", player.username));
            return true;
        }
        else if (userInputMessage.equals(".gp")){
            sendMessage(new Message(MessageType.MESSAGE, ".gp", player.username));
        }
        else if (userInputMessage.contains(".h")) {
            System.out.println("HElP> here is the help");
        }
        else {
            sendMessage(new Message(MessageType.MESSAGE, userInputMessage, player.username));
        }

        return false;
    }
}
