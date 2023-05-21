package client;

import utils.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientServerListener extends Thread {
    private String username;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    public boolean isRunning;

    public ClientServerListener(String username, ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException {
        this.username = username;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        isRunning = true;
    }


    @Override
    public void run() {
        while (isRunning) {
            try {
                Message message = (Message) inputStream.readObject();

                switch (message.getMessageType()) {
                    case MESSAGE:
                            System.out.println("\n" + message.getTokenUsername() + "> " + message.getMessageBody());
                            System.out.print(username + "> ");

                        break;
                    case LOGIN:
                        if (message.getMessageBody().equals("UNSUCCESSFUL")) {
                            System.out.println("\nServer> Authentication.Login Failed!");
                            isRunning = false;
                            System.exit(0);
                        } else if (message.getMessageBody().equals("SUCCESSFUL")) {
                            System.out.println("\nServer> Authentication.Login Success!");
                            Client.clientDB.saveToken(message.getTokenUsername());
                            break;
                        }
                    case REGISTER:
                        if (message.getMessageBody().equals("UNSUCCESSFUL")) {
                            System.out.println("\nServer> Authentication.Register Failed!");
                        } else if (message.getMessageBody().equals("SUCCESSFUL")) {
                            System.out.println("\nServer> Authentication.Register Success!");
                            Client.clientDB.saveToken(message.getTokenUsername());
                        }
                        isRunning = false;
                        System.exit(0);
                }
            } catch (Exception e) {
                System.out.println("Server has close the connection: " + e);
                System.exit(0);
            }
        }
    }


    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
