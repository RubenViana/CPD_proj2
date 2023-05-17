package client;

import models.ClientModel;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        ConnectionInfo connectionInfo = createConnectionInformation(args);

        if(connectionInfo != null ) {
            PlayerClient playerClient = new PlayerClient(connectionInfo);

            if(playerClient.initialize()) {

                Scanner scan = new Scanner(System.in);

                //DisplayUtil.displayHelp();

                while(playerClient.serverListener.isRunning) {

                    //DisplayUtil.displayPrompt(warriorClient.getUsername());
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
        if (args.length < 4) {
            System.out.println("Usage: java Client <host> <port> <username> <password>");
            return null;
        }
        ClientModel client = new ClientModel(args[2], args[3], "");

        ConnectionInfo connectionInformation = new ConnectionInfo(args[0], args[1], client);


        return connectionInformation;
    }
}