package server;

import models.ClientModel;
import utils.Message;

import java.util.ArrayList;

public class Authentication {
    private ServerDatabase serverDatabase;

    public Authentication () {
        this.serverDatabase = new ServerDatabase("server/database.txt");
    }

    public ClientModel login(Message message) {
        String[] info = message.getMessageBody().split(" ");

        //TODO: verify is token is valid, if valid -> assumeReconnect else login and assign new token

        ClientModel client = serverDatabase.getUser(info[0]);
        if (client == null){
            return null;
        }
        else if (info[1].equals(client.password)) {
            return client;
        }
        return null;
    }

    public ClientModel register(Message message){
        //TODO: implement register
        return null;
    }
}
