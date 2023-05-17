package server;

import models.ClientModel;

import java.io.*;
import java.util.TreeMap;

public class ServerDatabase {
    String filepath;
    private TreeMap<String, ClientModel> clients = new TreeMap<>();
    public ServerDatabase(String filepath) {
        this.filepath = filepath;
        this.loadDB();
    }

    private void loadDB() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.filepath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(";");
                String username = parts[0];
                String password = parts[1];
                String token = parts[2];
                this.clients.put(username, new ClientModel(username, password, token));
            }
        } catch (IOException e) {
            System.out.println("Error loading the database");
            e.printStackTrace();
        }
    }

    private void saveDB() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filepath))) {
            for (ClientModel client : this.clients.values()) {
                bw.write(client.username + ";" + client.password + ";" + client.token + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientModel getUser(String username) {
        return this.clients.get(username);
    }

    /*public void updateToken(String username, String newToken) {
        this.clients.get(username).setLastToken(newToken);
    }*/

}
