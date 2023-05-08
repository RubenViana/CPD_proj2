package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerClient {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private String server;
    private String username;
    private int port;
    ClientServerListener serverListener;

    public PlayerClient(ConnectionInfo connectionInformation) {
        this.server = connectionInformation.getServerAddress();
        this.port = connectionInformation.getPortNumber();
        this.username = connectionInformation.getUserName();
    }

    public boolean initialize() {

        try {
            initializeSocket();

            initializeInputOutputObjectStreams();

            serverListener = new ClientServerListener(username, inputStream, outputStream);
            serverListener.start();


        } catch (IOException e) {
            serverListener.setRunning(false);
            disconnect();
            return false;
        }

        return true;
    }

    public void sendMessage(String msg) {
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
        return username;
    }


    public boolean processInputMessage(String userInputMessage) {
        /*if (userInputMessage.equalsIgnoreCase(MessageType.LOGOUT.getShortValue())) {

            sendMessage(new ChatMessage(MessageType.LOGOUT, ""));
            return true;

        } else if (userInputMessage.contains(MessageType.LOAD_WARRIOR.getShortValue())) {

            loadWarrior(userInputMessage);

        } else if (userInputMessage.contains(MessageType.HELP.getShortValue())) {

            DisplayUtil.displayHelp();

        } else if (userInputMessage.contains(MessageType.WHOISIN.getShortValue())) {

            processWhoIsInCommand(userInputMessage);

        } else if (userInputMessage.contains(MessageType.ATTACK.getShortValue())) {

            processActionCommand(userInputMessage, MessageType.ATTACK);

        } else if (userInputMessage.contains(MessageType.DEFEND.getShortValue())) {

            processActionCommand(userInputMessage, MessageType.DEFEND);

        } else if (userInputMessage.contains(MessageType.STATISTIC.getShortValue())) {

            displayWarriorStatistic();

        } else {
            sendMessage(new ChatMessage(MessageType.MESSAGE, userInputMessage));
        }*/

        sendMessage(userInputMessage);
        return false;
    }
}
