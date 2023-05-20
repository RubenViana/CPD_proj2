package client;

import models.ClientModel;
import utils.Message;
import utils.MessageType;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static ClientDatabase clientDB;
    public static void main(String[] args) {

        ConnectionInfo connectionInfo = createConnectionInformation(args);

        if(connectionInfo != null ) {
            PlayerClient playerClient = new PlayerClient(connectionInfo);

            if(playerClient.initialize()) {

                //Authentication Message
                if (args[2].equals("-l"))
                    playerClient.sendMessage(new Message(MessageType.LOGIN, playerClient.player.username + " " + playerClient.player.password, playerClient.player.token));
                else if (args[2].equals("-r"))
                    playerClient.sendMessage(new Message(MessageType.REGISTER, playerClient.player.username + " " + playerClient.player.password, playerClient.player.token));

                Scanner scan = new Scanner(System.in);

                while(playerClient.serverListener.isRunning) {

                    System.out.print(playerClient.getUsername() + "> ");
                    String userInputMessage = scan.nextLine();

                    if(playerClient.processInputMessage(userInputMessage)) break;

                }

                playerClient.disconnect();
                System.exit(0);

            }

        }

    }

    private static ConnectionInfo createConnectionInformation(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage: java Client <host> <port> <-l | -r> <username> <password>");
            return null;
        }

        clientDB = new ClientDatabase("client/" + args[3] + "-Token.txt");

        ClientModel client = new ClientModel(args[3], args[4], clientDB.loadToken());

        ConnectionInfo connectionInformation = new ConnectionInfo(args[0], args[1], client);


        return connectionInformation;
    }
}