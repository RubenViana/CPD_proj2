package server;

import models.ClientModel;
import utils.Message;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

public class Authentication {
    public boolean isReconnect = false;
    public Authentication () {
    }

    public ClientModel login(Message message){
        String[] info = message.getMessageBody().split(" ");

        ClientModel client = Server.serverDB.getUser(info[0]);
        if (client == null){
            return null;
        }
        else if (info[1].equals(client.password)) {
            if (verifyToken(message.getTokenUsername()) && message.getTokenUsername().equals(client.token)) {
                isReconnect = true;
            }
            else {
                String token = generateToken();
                Server.serverDB.updateToken(client.username, token);
            }
            return client;
        }
        return null;
    }

    public boolean register(Message message){
        String[] info = message.getMessageBody().split(" ");

        return Server.serverDB.registerNewClient(info[0], info[1]);
    }

    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[16];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public static boolean verifyToken(String token) {
        try {
            Base64.getUrlDecoder().decode(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
