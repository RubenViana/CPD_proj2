package server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    public static ServerDatabase serverDB = new ServerDatabase("server/database.txt");
    private static int CONNECTION_ID;
    private static int N_GAMES;
    public static ArrayList<PlayerHandler> playerQueue = new ArrayList<PlayerHandler>();
    public static ArrayList<Game> activeGames = new ArrayList<Game>();
    public static ReentrantLock lock = new ReentrantLock();
    private Date startTime;
    public static SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm:ss");
    private boolean isRunning;
    private int port;
    private ExecutorService pool = Executors.newFixedThreadPool(500);
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static void main(String[] args) {
        try {
            new Server(args);
        } catch (Exception e) {
            System.out.println("Error in Server: " + e);
        }
    }
    public Server(String[] args) throws Exception {

        if (args.length < 1) return;
        port = Integer.parseInt(args[0]);

        isRunning = true;
        startTime = new Date();

        System.out.println("            SERVER STARTED          ");
        System.out.println("------------------------------------");
        System.out.println(".c to list available commands");
        try (var listener = new ServerSocket(port)) {
            pool.execute(new serverAdmin());
            while (isRunning) {
                scheduler.scheduleAtFixedRate(() -> {
                    try {
                        lock.lock();
                        if (playerQueue.size() >= 5) {
                            System.out.println("[" + displayTime.format(new Date()) + "] Starting Game " + ++N_GAMES);
                            ArrayList<PlayerHandler> gamePlayers = new ArrayList<PlayerHandler>();

                            for (int i = 0; i < 5; i++) {
                                gamePlayers.add(playerQueue.get(0));
                                removeFromQueue(playerQueue.get(0));
                            }
                            Game game = new Game(gamePlayers, N_GAMES);
                            activeGames.add(game);
                        }
                    } finally {
                        lock.unlock();
                    }
                }, 0, 5, TimeUnit.SECONDS);
                Socket client = listener.accept();
                System.out.println("[" + displayTime.format(new Date()) + "] New connection: " + client);
                PlayerHandler handler = new PlayerHandler(client);
                pool.execute(handler);
            }
            //save DB
            //serverDB.saveDB();
        } catch (Exception e) {
            String msg = (new Date()) + " Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
        }
    }

    public static void addToQueue(PlayerHandler playerHandler) {
        try {
            lock.lock();
            playerQueue.add(playerHandler);
        } finally {
            lock.unlock();
        }
    }
    public static void removeFromQueue(PlayerHandler playerHandler) {
        try {
            lock.lock();
            playerQueue.remove(playerHandler);
        } finally {
            lock.unlock();
        }
    }

    public String timeAlive(){
        return displayTime.format(new Date(new Date().getTime() - startTime.getTime() - 3600000));
    }

    private class serverAdmin implements Runnable {
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (isRunning) {
                //System.out.print("admin> ");
                String[] input = scanner.nextLine().split(" ");
                switch (input[0]){
                    case ".c":
                        System.out.println("COMMANDS");
                        System.out.println("    .si -> Display Server Info");
                        System.out.println("    .sd -> Shutdown Server");
                        System.out.println("    .lq -> List Player Queue");
                        System.out.println("    .lg -> List Active Games");
                        System.out.println("    .eg <gameId> -> End Game");
                        break;
                    case ".si":
                        System.out.println("SERVER INFO");
                        System.out.println("    - Host: localhost");
                        System.out.println("    - Port: " + port);
                        System.out.println("    - Time Alive: " + timeAlive());
                        break;
                    case ".sd":
                        System.out.println("[" + displayTime.format(new Date()) + "] Shutting Down Server...");
                        isRunning = false;
                        System.exit(0);
                        break;
                    case ".lq":
                        System.out.println("PLAYERS QUEUE");
                        for (PlayerHandler playerHandler : playerQueue)
                            System.out.println("    - " + playerHandler.player.username);
                        break;
                    case ".lg":
                        System.out.println("ACTIVE GAMES");
                        for (Game g : activeGames) {
                            System.out.println("    - Game " + g.gameNumber);
                            System.out.println("        - Time Alive: " + g.timeAlive());
                            System.out.println("        - Players:");
                            for (PlayerHandler p : g.players)
                                System.out.println("            - " + p.player.username);
                        }
                        break;
                    case ".eg":
                        for (Game g : activeGames){
                            if (g.gameNumber == Integer.parseInt(input[1])) {
                                g.close();
                                break;
                            }
                        }
                        break;
                }
            }
        }
    }
}