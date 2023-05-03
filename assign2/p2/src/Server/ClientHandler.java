package Server;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class ClientHandler extends Thread
{
    final DataInputStream inputStream;
    final DataOutputStream outputStream;
    final Socket socket;


    // Constructor
    public ClientHandler(Socket s, DataInputStream is, DataOutputStream os)
    {
        this.socket = s;
        this.inputStream = is;
        this.outputStream = os;
    }

    @Override
    public void run()
    {
        String received;
        String toreturn;
        while (true)
        {
            try {
                // Ask user what he wants
                outputStream.writeUTF("Server> Type exit to terminate connection.");

                // receive the answer from client
                received = inputStream.readUTF();

                if(received.equals("exit"))
                {
                    System.out.println("Client " + this.socket + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                }

                // write on output stream based on the
                // answer from the client

                System.out.println("Client> " + received);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // closing resources
            this.inputStream.close();
            this.outputStream.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
