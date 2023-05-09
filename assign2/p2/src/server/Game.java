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

public class Game {

    public ArrayList<PlayerHandler> players;

    public int gameNumber;

    private int nRounds;

    private SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm:ss");

    /**
     * Constructs a handler thread, squirreling away the socket. All the interesting
     * work is done in the run method. Remember the constructor is called from the
     * server's main method, so this has to be as short as possible.
     */
    public Game(ArrayList<PlayerHandler> clients, int gameNumber) {
        this.players = clients;
        this.gameNumber = gameNumber;
        for (PlayerHandler player : players)
            player.write(new Message(MessageType.MESSAGE, "=== GAME " + gameNumber + " ===", "Game" + gameNumber ));
    }

    public synchronized boolean guess (String guess){
        if (guess.equals("guess")) {
            nRounds += 1;
            return true;
        }
        return false;
    }

}
