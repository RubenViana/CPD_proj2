package client;

import utils.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientServerListener extends Thread {
    private String username;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean isRunning;

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
                        System.out.println("\n" + message.getToken() + "> " + message.getMessageBody());
                        System.out.print(username + "> ");
                        break;
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
