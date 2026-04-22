package socket;

import java.io.*;
import java.net.*;

public class TollClient {

    private static final String HOST = "localhost";
    private static final int    PORT = 9999;

    private Socket         socket;
    private PrintWriter    out;
    private BufferedReader in;
    private MessageListener listener;

    
    public interface MessageListener {
        void onMessageReceived(String message);
    }

    public TollClient(MessageListener listener) {
        this.listener = listener;
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(HOST, PORT);
            out    = new PrintWriter(socket.getOutputStream(), true);
            in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("✅ Connected to Toll Server.");

            
            Thread listenerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        final String msg = message;
                       
                        if (listener != null) listener.onMessageReceived(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Listener stopped: " + e.getMessage());
                }
            });
            listenerThread.setDaemon(true); 
            listenerThread.start();

        } catch (IOException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
    }

    
    public void sendMessage(String message) {
        if (out != null) out.println(message);
    }

    public void disconnect() {
        try { if (socket != null) socket.close(); }
        catch (IOException e) { System.out.println("Disconnect error: " + e.getMessage()); }
    }
}