package client;

import models.Player;

public class ConnectionInfo {
    private int portNumber;
    private String serverAddress;

    public Player player;

    public ConnectionInfo(String serverAddress, String portNumber, Player player) {
        this.portNumber = Integer.parseInt(portNumber);
        this.serverAddress = serverAddress;
        this.player = player;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getServerAddress() {
        return serverAddress;
    }

}
