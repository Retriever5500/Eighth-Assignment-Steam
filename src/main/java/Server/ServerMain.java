package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

public class ServerMain {

    private static ServerSocket server = null;

    public static void main(String[] args) {
        initialize();
        run();
    }

    public static void initialize() {
        try {
            server = new ServerSocket(1111);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void run() {
        DatabaseManager database = new DatabaseManager("jdbc:sqlite:Database Backup/steam.db");

        while(true) {
            Socket clientSocket;
            try {
                System.out.println("LOG: Waiting for a client...");
                clientSocket = server.accept();
                System.out.println("LOG: Connected to client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                Thread clientHandler = new Thread(new ClientHandler(clientSocket, database));
                clientHandler.start();
                
            } catch(IOException e) {
                e.printStackTrace();
            }

            

        }
    }
}
