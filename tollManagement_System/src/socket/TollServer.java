package socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class TollServer {

    private static final int PORT = 9999;

    
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("✅ Toll Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔌 New client connected: " + clientSocket.getInetAddress());

               
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    
    public static synchronized void broadcast(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
        System.out.println("📢 Broadcasted: " + message);
    }

    
    public static synchronized void removeClient(PrintWriter writer) {
        clientWriters.remove(writer);
        System.out.println("❌ A client disconnected.");
    }

    
    public static synchronized void addClient(PrintWriter writer) {
        clientWriters.add(writer);
    }
}