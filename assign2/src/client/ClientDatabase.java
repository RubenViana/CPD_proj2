package client;

import models.ClientModel;

import java.io.*;
import java.util.TreeMap;

public class ClientDatabase {
    String filepath;
    public ClientDatabase(String filepath) {
        this.filepath = filepath;
    }

    public String loadToken() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.filepath))) {
            String token;
            if ((token = bufferedReader.readLine()) != null) {
                return token;
            }
        } catch (IOException e) {
            System.out.println("Error loading token from database");
        }
        return "-";
    }

    public void saveToken(String newToken) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filepath))) {
            bw.write(newToken);
        } catch (IOException e) {
            System.out.println("Error saving token into database");
        }
    }
}

