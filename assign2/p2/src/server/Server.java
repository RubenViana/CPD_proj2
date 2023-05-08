package server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

public class Server {

    private static int CONNECTION_ID;

    private static List<Handler> clients = new ArrayList<Handler>();

    public static void main(String[] args) throws Exception {

        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        System.out.println("The server is running on port " + port + " ...");
        var pool = Executors.newFixedThreadPool(500);
        try (var listener = new ServerSocket(port)) {
            while (true) {
                Handler handler = new Handler(listener.accept(), ++CONNECTION_ID);
                pool.execute(handler);
                clients.add(handler);
            }
        } catch (Exception e) {
            String msg = (new Date()) + " Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
            //DisplayUtil.displayEvent(msg);
        }

    }


    /**
     * The client handler task.
     */
    private static class Handler implements Runnable {
        private Socket socket;
        private ObjectInputStream inputStream;

        private ObjectOutputStream outputStream;

        private int threadId;

        private String username;

        private String dateInString;

        private boolean isRunning = true;

        private SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm:ss");

        /**
         * Constructs a handler thread, squirreling away the socket. All the interesting
         * work is done in the run method. Remember the constructor is called from the
         * server's main method, so this has to be as short as possible.
         */
        public Handler(Socket socket, int threadId) throws Exception{

            this.socket = socket;
            this.threadId = threadId;

            initializeInputOutputStreams(socket);

            username = (String) inputStream.readObject();

            //DisplayUtil.displayEvent(username + " just connected.");
            System.out.println(username + " just connected.");
            dateInString = new Date().toString();

        }

        /**
         * Services this thread's client by repeatedly requesting a screen name until a
         * unique one has been submitted, then acknowledges the name and registers the
         * output stream for the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {

            while (isRunning) {
                try {
                    String chatMessage = (String) inputStream.readObject();
                    System.out.println(username + "> " + chatMessage);

                    for (Handler client : clients) {
                        if (client == this) continue;
                        client.outputStream.writeObject(username + "> " + chatMessage);
                    }

                    /*switch (chatMessage.getMessageType()) {

                        case MESSAGE:
                            broadcastMessageToAllClients(username + ": " + chatMessage.getMessage());
                            break;
                        case LOGOUT:
                            DisplayUtil.displayEvent(username + " disconnected with a LOGOUT message.");
                            isRunning = false;
                            break;
                        case LOAD_WARRIOR:
                            processLoadWarrior(chatMessage);
                            break;
                        case ATTACK:
                            processAction(chatMessage, MessageType.ATTACK);
                            break;
                        case DEFEND:
                            processAction(chatMessage, MessageType.DEFEND);
                            break;
                        case WHOISIN:
                            displayWarriorInfo(chatMessage.getMessage());
                            break;
                    }*/
                } catch (Exception e) {
                    //DisplayUtil.displayEvent(username + " Exception reading Streams: " + e);
                    System.out.println(username + " Exception reading Streams: " + e);
                    break;
                }
            }

            // remove myself from the arrayList containing the list of the
            // connected Clients


            closeAllResource();
        }

        private void initializeInputOutputStreams(Socket socket) throws IOException {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        }

        public void closeAllResource() {
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (socket != null) socket.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e);
                //DisplayUtil.displayEvent(username + " Error closing resources: " + e);
            }
        }

        /*private synchronized void broadcastMessageToAllClients(String message) {

            *//*ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE, message);

            DisplayUtil.displayBroadcastMessage(chatMessage);*//*


            // we loop in reverse order in case we would have to remove a Client
            // because it has disconnected
            for (int i = serverClients.size(); --i >= 0; ) {
                Handler serverThread = serverClients.get(i);
                // try to write to the Client if it fails removeClientThreadFromListById it from the list
                if (!serverThread.writeMsg(message)) {
                    serverClients.remove(i);
                    System.out.println("Disconnected client " + serverThread.username + " removed from list.");
                    //DisplayUtil.displayEvent(String.format("Disconnected client %s removed from list.", serverThread.username));
                }
            }
        }*/

        /*private boolean writeMsg(String chatMessage) {

            if (!socket.isConnected()) {
                closeAllResource();
                return false;
            }

            try {

                outputStream.writeObject(chatMessage);

            } catch (IOException e) {
                // if an error occurs, do not abort just inform the user
                System.out.println("Error sending message to " + username);
                *//*DisplayUtil.displayEvent("Error sending message to " + username);
                DisplayUtil.displayEvent(e.toString());*//*
            }
            return true;
        }*/

    }
}