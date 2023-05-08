package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class Server {

    private static ArrayList<ClientHandler> serverClients = new ArrayList<ClientHandler>();
    public static void main(String[] args) throws IOException {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;
 
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("Server is listening on port " + port);
 
            while (true) {
                Socket client = serverSocket.accept();

                System.out.println("A new client is connected : " + client);

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                ClientHandler clientSock = new ClientHandler(client);

                // add client to serverClients
                serverClients.add(clientSock);

                // This thread will handle the client
                // separately
                new Thread(clientSock).start();

            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}