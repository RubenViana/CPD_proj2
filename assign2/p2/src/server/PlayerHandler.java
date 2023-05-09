package server;

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
    public String username; //TODO: add more info here!
    private boolean isRunning = true;
    private Game game;

    private ArrayList<PlayerHandler> otherPlayers = new ArrayList<PlayerHandler>();

    public PlayerHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream, String username) throws IOException{
        this.username = username;
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void run() {
        while (isRunning) {
            try {
                Message message = (Message) inputStream.readObject();
                switch (message.getMessageType()) {
                    case MESSAGE:
                        System.out.println("[Game" + game.gameNumber + "] " + username + "> " + message.getMessageBody());
                        if (game.guess(message.getMessageBody())){
                            System.out.println("[Game" + game.gameNumber + "] " + username + " guessed the word!");
                            write(new Message(MessageType.MESSAGE, username + " You the word!", "Game" + game.gameNumber));
                            broadcastMessageToAllClients(new Message(MessageType.MESSAGE, username + " guessed the word!", "Game" + game.gameNumber));
                        }
                        break;
                    case DISCONNECT:
                        System.out.println(username + " disconnected with a DISCONNECT message.");
                        isRunning = false;
                        break;
                }
            } catch (Exception e) {
                System.out.println(username + " Exception reading Streams: " + e);
                break;
            }
        }

        // remove myself from the arrayList containing the list of the
        // connected Clients
        //removeClientThreadFromListById(threadId);

        closeAllResource();
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
            System.out.println("Error sending message to " + username);
        }
        return true;
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

    public void setOtherPlayers (ArrayList<PlayerHandler> players) {
        otherPlayers = players;
    }

    public void setGame (Game game) {
        this.game = game;
        this.otherPlayers = game.players;
    }

    private synchronized void broadcastMessageToAllClients(Message message) {
        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for (int i = otherPlayers.size(); --i >= 0; ) {
            PlayerHandler client = otherPlayers.get(i);
            if (client == this) continue;
            // try to write to the Client if it fails removeClientThreadFromListById it from the list
            if (!client.write(message)) {
                otherPlayers.remove(i);
                System.out.println("Disconnected client " + client.username + " removed from list.");
            }
        }
    }

    /*private synchronized void removeClientThreadFromListById(int id) {
        for (int i = 0; i < clients.size(); ++i) {

            Server.Handler serverThread = clients.get(i);
            // found it
            if (serverThread.threadId == id) {
                closeAllResource();
                clients.remove(i);
                return;
            }
        }
    }*/
}
