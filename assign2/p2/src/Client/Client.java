package Client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    String serverAddress;
    Integer serverPort;
    Scanner in;
    PrintWriter out;
    String name;

    /**
     * Constructs the client by laying out the GUI and registering a listener with
     * the textfield so that pressing Return in the listener sends the textfield
     * contents to the server. Note however that the textfield is initially NOT
     * editable, and only becomes editable AFTER the client receives the
     * NAMEACCEPTED message from the server.
     */
    public Client(String serverAddress, String serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = Integer.parseInt(serverPort);
    }


    private void run() throws IOException {
        try {
            var socket = new Socket(serverAddress, serverPort);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                var line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    System.out.println("Server> Choose a server name!");
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.name = line.substring(13);
                } else if (line.startsWith("MESSAGE")) {
                    System.out.println(this.name + "> " + line.substring(8));
                }
            }
        } finally {
            System.out.println("\n");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        var client = new Client(args[0], args[1]);
        client.run();
    }
}