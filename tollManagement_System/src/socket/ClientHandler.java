package socket;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket      clientSocket;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            
            BufferedReader in  = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            
            TollServer.addClient(out);

            String message;
            
            while ((message = in.readLine()) != null) {
                System.out.println("📨 Received: " + message);
                
                TollServer.broadcast(message);
            }

        } catch (IOException e) {
            System.out.println("Client handler error: " + e.getMessage());
        } finally {
           
            if (out != null) TollServer.removeClient(out);
            try { clientSocket.close(); } catch (IOException e) {}
        }
    }
}