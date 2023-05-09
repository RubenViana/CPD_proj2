package server;

import utils.Message;
import utils.MessageType;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private static int CONNECTION_ID;
    private static int N_GAMES;

    private static List<PlayerHandler> clientsQueue = new ArrayList<PlayerHandler>();

    public static void main(String[] args) throws Exception {

        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        System.out.println("The server is running on port " + port + " ...");
        var pool = Executors.newFixedThreadPool(500);
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        try (var listener = new ServerSocket(port)) {
            while (true) {
                scheduler.scheduleAtFixedRate(() -> {
                            if (clientsQueue.size() >= 2) {
                                System.out.println("Creating Game " + ++N_GAMES);
                                ArrayList<PlayerHandler> gamePlayers = new ArrayList<PlayerHandler>();
                                for (int i = 0; i < 2; i++) {
                                    gamePlayers.add(clientsQueue.get(0));
                                    clientsQueue.remove(0);
                                }
                                Game game = new Game(gamePlayers, N_GAMES);
                                for (PlayerHandler player : gamePlayers) {
                                    player.setGame(game);
                                }

                                for (PlayerHandler player : game.players)
                                    pool.execute(player);
                            }
                        }, 0, 5, TimeUnit.SECONDS);
                AuthHandler handler = new AuthHandler(listener.accept(), ++CONNECTION_ID);
                pool.execute(handler);
            }
        } catch (Exception e) {
            String msg = (new Date()) + " Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
            //DisplayUtil.displayEvent(msg);
        }
    }

    private static class AuthHandler implements Runnable{
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
        public AuthHandler(Socket socket, int threadId) throws Exception{

            this.socket = socket;
            this.threadId = threadId;

            initializeInputOutputStreams(socket);

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
                    Message message = (Message) inputStream.readObject();

                    switch (message.getMessageType()) {
                        case LOGIN:
                            //TODO: deal with login! separate class Auth...
                            System.out.println("Login");
                            username = message.getMessageBody();
                            boolean login = true; //testing only
                            if (login == true) {
                                write(new Message(MessageType.MESSAGE, "Login Successful", "server"));
                                System.out.println("[" + displayTime.format(new Date()) + "] " + username + " just logged in and added to queue");
                                clientsQueue.add(new PlayerHandler(socket, inputStream, outputStream, username));
                                return;
                            }
                            break;
                        case REGISTER:
                            //TODO: deal with register! separate class Auth...
                            System.out.println("Register");
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    System.out.println(socket + " Exception reading Streams: " + e);
                    break;
                }
            }
        }

        private void initializeInputOutputStreams(Socket socket) throws IOException {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        }

        private boolean write(Message message) {
            try {
                outputStream.writeObject(message);
            } catch (IOException e) {
                // if an error occurs, do not abort just inform the user
                System.out.println("Error sending message to " + socket);
            }
            return true;
        }

    }

}