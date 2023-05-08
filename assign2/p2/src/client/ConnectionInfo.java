package client;

public class ConnectionInfo {
    private int portNumber;
    private String serverAddress;
    private String userName;

    public ConnectionInfo(String serverAddress, String portNumber, String userName) {
        this.portNumber = Integer.parseInt(portNumber);
        this.serverAddress = serverAddress;
        this.userName = userName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getUserName() {
        return userName;
    }

}
