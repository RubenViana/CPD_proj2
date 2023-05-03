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

        try
        {
            Scanner scn = new Scanner(System.in);

            // establish the connection with server port 5056
            Socket socket = new Socket(hostname, port);

            // obtaining input and out streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true)
            {
                System.out.println(inputStream.readUTF());
                System.out.print("Client> ");
                String tosend = scn.nextLine();
                outputStream.writeUTF(tosend);

                // If client sends exit,close this connection
                // and then break from the while loop
                if(tosend.equals("exit"))
                {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }

            }

            // closing resources
            scn.close();
            inputStream.close();
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}