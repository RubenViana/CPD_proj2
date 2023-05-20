package server;

import utils.Message;
import utils.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {

    public ArrayList<PlayerHandler> players;

    public int gameNumber;
    private Date startTime;
    private SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm:ss");

    /**
     * Constructs a handler thread, squirreling away the socket. All the interesting
     * work is done in the run method. Remember the constructor is called from the
     * server's main method, so this has to be as short as possible.
     */
    public Game(ArrayList<PlayerHandler> clients, int gameNumber) {
        this.players = clients;
        this.gameNumber = gameNumber;
        startTime = new Date();
        for (PlayerHandler player : players) {
            player.game = this;
        }
        broadcastMessage(new Message(MessageType.MESSAGE, "Game " + gameNumber + " started", "Server"));
        /*scheduler.scheduleAtFixedRate(() -> {
            broadcastMessage(new Message(MessageType.MESSAGE, "BLAH", "Game" + gameNumber));
        }, 0, 5, TimeUnit.SECONDS);*/
    }

    public String timeAlive(){
        return displayTime.format(new Date(new Date().getTime() - startTime.getTime() - 3600000));
    }

    public boolean addPlayer(PlayerHandler player) {
        try {
            Server.lock.lock();
            if (!players.contains(player)){
                players.add(player);
                return true;
            }
            return false;
        }
        finally {
            Server.lock.unlock();
        }

    }

    public boolean removePlayer(PlayerHandler player) {
        try {
            Server.lock.lock();
            if (players.contains(player)){
                players.remove(player);
                return true;
            }
            return false;
        }
        finally {
            Server.lock.unlock();
        }

    }

    public void broadcastMessage(Message message) {
        for (PlayerHandler player : players)
            player.write(message);
    }

    public void close() {
        System.out.println("[" + displayTime.format(new Date()) + "] " + "Game " + gameNumber + " has ended");
        broadcastMessage(new Message(MessageType.MESSAGE, "Game " + gameNumber + " has ended", "Server"));

        for (PlayerHandler player : players)
            player.closeAllResource();

        players.clear();

        Server.activeGames.remove(this);
    }
}
