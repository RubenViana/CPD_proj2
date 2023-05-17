package client;

import models.ClientModel;

public class ConnectionInfo {
    private int portNumber;
    private String serverAddress;

    public ClientModel client;

    public ConnectionInfo(String serverAddress, String portNumber, ClientModel client) {
        this.portNumber = Integer.parseInt(portNumber);
        this.serverAddress = serverAddress;
        this.client = client;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getServerAddress() {
        return serverAddress;
    }

}
