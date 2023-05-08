package Client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * This program demonstrates a simple TCP/IP socket client.
 *
 * @author www.codejava.net
 */
public class Client {
 
    public static void main(String[] args) throws IOException{
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        // establish a connection by providing host and port
        // number
        try (Socket socket = new Socket(hostname, port)) {

            // writing to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // reading from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // object of scanner class
            Scanner sc = new Scanner(System.in);

            String line = null;

            while (true) {
                // displaying server reply
                if ((in.ready()))
                    System.out.println("Server> " + in.readLine());

                // reading from user

                line = sc.nextLine();
                // sending the user input to server
                out.println(line);
                out.flush();

                if (line.equals("exit")) {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }

            }

            // closing the scanner, in, out objects
            sc.close();
            in.close();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}