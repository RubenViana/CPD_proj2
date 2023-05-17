package server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private static int CONNECTION_ID;
    private static int N_GAMES;
    public static ArrayList<PlayerHandler> playerQueue = new ArrayList<PlayerHandler>();

    public static ArrayList<Game> activeGames = new ArrayList<Game>();

    public static void main(String[] args) {
        try {
            new Server(args);
        } catch (Exception e) {
            System.out.println("Error in Server: " + e);
        }
    }
    public Server(String[] args) throws Exception {

        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        System.out.println("The server is running on port " + port + " ...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        try (var listener = new ServerSocket(port)) {
            pool.execute(new serverAdmin());
            while (true) {
                scheduler.scheduleAtFixedRate(() -> {
                    if (playerQueue.size() >= 2) {
                        System.out.println("Creating Game " + ++N_GAMES);
                        ArrayList<PlayerHandler> gamePlayers = new ArrayList<PlayerHandler>();
                        for (int i = 0; i < 2; i++) {
                            gamePlayers.add(playerQueue.get(0));
                            playerQueue.remove(0);
                        }
                        Game game = new Game(gamePlayers, N_GAMES);
                        activeGames.add(game);
                    }
                }, 0, 5, TimeUnit.SECONDS);
                Socket client = listener.accept();
                System.out.println("New connection: " + client);
                PlayerHandler handler = new PlayerHandler(client);
                pool.execute(handler);
            }
        } catch (Exception e) {
            String msg = (new Date()) + " Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
        }
    }

    public static void addToQueue(PlayerHandler playerHandler) {
        playerQueue.add(playerHandler);
    }

    private class serverAdmin implements Runnable {
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if (input.equals("exit")) {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                else if (input.equals("games")) {
                    System.out.println("Number Active Games: " + activeGames.size());
                }
                //TODO: process input commands such as "exit", "list_games", "list_players", etc.
            }
        }
    }
}