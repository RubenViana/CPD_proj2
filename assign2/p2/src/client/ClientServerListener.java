package client;

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

        sendUsernameToServer();
    }


    @Override
    public void run() {
        while (isRunning) {
            try {
                String chatMessage = (String) inputStream.readObject();
                System.out.println("\n" + chatMessage);
                System.out.print(username + "> ");

                /*ActionMessage actionMessage = null;
                switch (chatMessage.getMessageType()) {

                    case MESSAGE:
                        DisplayUtil.displayEvent(chatMessage.getMessage());
                        DisplayUtil.displayPrompt(username);
                        break;
                    case ATTACK_NOTIFY:
                        actionMessage = chatMessage.getActionMessage();
                        warrior.reduceHealthPoints(actionMessage.getActionPoint());
                        break;
                    case DEFEND_NOTIFY:
                        actionMessage = chatMessage.getActionMessage();
                        warrior.addHealthPoints(actionMessage.getActionPoint());
                        break;
                    case WARRIOR_LOADED:
                        warrior = chatMessage.getWarrior();
                        DisplayUtil.displayEvent(String.format("warrior [%s] loaded.", warrior));
                        break;
                    case WARRIOR_DEATH_NOTIFY:
                        DisplayUtil.displayEvent(String.format("warrior [%s] is dead.", warrior.getName()));
                        warrior = null;
                        break;
                    }*/
            } catch (Exception e) {
                System.out.println("Server has close the connection: " + e);
                System.exit(0);
            }
        }
    }


    private void sendUsernameToServer() throws IOException {
        try {
            outputStream.writeObject(username);
        } catch (IOException e) {
            System.out.println("Exception doing login : " + e);
        }
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
