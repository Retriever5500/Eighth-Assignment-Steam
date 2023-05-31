package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.json.JSONObject;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DatabaseManager database;

    private String clientIPAddress;
    private DataOutputStream output;
    private DataInputStream input;

    public ClientHandler(Socket clientSocket, DatabaseManager database) throws IOException {
        this.clientSocket = clientSocket;
        this.clientIPAddress = clientSocket.getInetAddress() + ":" + clientSocket.getPort();
        this.database = database;
        this.output = new DataOutputStream(clientSocket.getOutputStream());
        this.input = new DataInputStream(clientSocket.getInputStream());
    }
    
    public void run() {
        while(true) {
            
            String requestString;
            try {
                requestString = input.readUTF();
                if(requestString.equals("Exit")) {
                    output.close();
                    input.close();
                    clientSocket.close();
                    break;
                }
                JSONObject request = new JSONObject(requestString);
                JSONObject response = RequestHandler.handleRequest(request, database, clientIPAddress);
                if(response.getString("responseType").equals("downloadGame")) {
                    String gamePath = "src/main/java/Server/" + response.getJSONObject("responseBody").getString("gamePath");
                    sendFile(gamePath);
                } else if (response != null) {
                    output.writeUTF(response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            

        }
    }

    private void sendFile(String path) {
        int bytes = 0;
        // Open the File where he located in your pc
        File file = new File(path);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
        // Here we send the File to Server
        try {
            output.writeLong(file.length());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Here we  break file into chunks
        byte[] buffer = new byte[4 * 1024];
        try {
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                // Send the file to Server Socket 
                output.write(buffer, 0, bytes);
                output.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // close the file here
        try {
            fileInputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
