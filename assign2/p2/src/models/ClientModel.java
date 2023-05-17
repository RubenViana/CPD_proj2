package models;

public class ClientModel {
    public String username;
    public String password;
    public String token;
    // Add any other necessary player data

    public ClientModel(String username, String password, String token) {
        this.username = username;
        this.token = token;
        this.password = password;
    }
}