package server;

import models.Player;
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

    private SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm:ss");
    //private Map<models.Player, Integer> scores;
    private ArrayList<String> questions = new ArrayList<String>();

    //private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructs a handler thread, squirreling away the socket. All the interesting
     * work is done in the run method. Remember the constructor is called from the
     * server's main method, so this has to be as short as possible.
     */
    public Game(ArrayList<PlayerHandler> clients, int gameNumber) {
        this.players = clients;
        this.gameNumber = gameNumber;
        //scores = new HashMap<>();
        loadQuestions();
        for (PlayerHandler player : players) {
            player.game = this;
        }
        broadcastMessage(new Message(MessageType.MESSAGE, "=== GAME " + gameNumber + " ===", "Game" + gameNumber));
        /*scheduler.scheduleAtFixedRate(() -> {
            broadcastMessage(new Message(MessageType.MESSAGE, "BLAH", "Game" + gameNumber));
        }, 0, 5, TimeUnit.SECONDS);*/
    }

    public boolean addPlayer(PlayerHandler player) {
        if (!players.contains(player)){
            players.add(player);
            return true;
        }
        return false;
    }

    public boolean removePlayer(PlayerHandler player) {
        if (players.contains(player)){
            players.remove(player);
            return true;
        }
        return false;
    }

    public void broadcastMessage(Message message) {
        for (PlayerHandler player : players)
            player.write(message);
    }

    public void close() {
        broadcastMessage(new Message(MessageType.MESSAGE, "Game " + gameNumber + " has ended", "Game" + gameNumber));
        System.out.println("Game " + gameNumber + " has ended");

        for (PlayerHandler player : players)
            player.closeAllResource();

        players.clear();

        Server.activeGames.remove(this);
    }


    private void loadQuestions() {
        // Shuffle and select a subset of questions from the question bank for this game
        /*Collections.shuffle(questionBank);
        questions.addAll(questionBank.subList(0, GAME_QUESTIONS));*/
        questions.add("What is the capital of France?");
        questions.add("What is the capital of Spain?");
        questions.add("What is the capital of Germany?");
    }

    private void sendQuestion(String question) {
        // TODO: Send the question to all players
        for (PlayerHandler player : players)
            player.write(new Message(MessageType.MESSAGE, question, "Game" + gameNumber));
    }

    private Map<Player, Integer> receiveAnswers() {
        // TODO: Receive answers from all players and return a map of player -> answer
        return null; // Placeholder
    }

    private void updateScores(String question, Map<Player, Integer> answers) {
        // TODO: Update scores based on the question and answers
    }

    public int getScore(Player player) {
        return 1; //scores.getOrDefault(player, 0);
    }


}
