package Server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class ClientHandler extends Thread
{
    final Socket socket;


    // Constructor
    public ClientHandler(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        PrintWriter out = null;
        BufferedReader in = null;
        try {

            // get the outputstream of client
            out = new PrintWriter(socket.getOutputStream(), true);

            // get the inputstream of client
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //send 1st msg to Client
            out.println("Hello from Server");

            // object of scanner class
            Scanner sc = new Scanner(System.in);
            String line = null;

            while (true) {

                // writing from server

                line = sc.nextLine();
                // sending the user input to server
                out.println(line);
                out.flush();
                


                if ((in.ready())) {
                    line = in.readLine();

                    if (line.equals("exit")) {
                        System.out.println("Client " + this.socket + " sends exit...");
                        System.out.println("Closing this connection.");
                        this.socket.close();
                        System.out.println("Connection closed");
                        break;
                    }

                    // writing the received message from
                    // client
                    System.out.println("Client> " + line);
                }


            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    socket.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

