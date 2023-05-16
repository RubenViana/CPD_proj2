package models;

public class Player {
    public String username;
    public String password;
    private String token;
    // Add any other necessary player data

    public Player(String username, String password, String token) {
        this.username = username;
        this.token = token;
        this.password = password;
    }
}