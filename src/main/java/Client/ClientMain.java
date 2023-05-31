package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {

    private static Socket socket = null;

    public static void main(String[] args) {

        String address = "127.0.0.1";
        int port = 1111;

        try {
            socket = new Socket(address, port);
 
            // takes input from terminal
            DataInputStream input = new DataInputStream(socket.getInputStream());
 
            // sends output to the socket
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            
            Menu.run(input, output);
        } catch (UnknownHostException u) {
            System.out.println("Could not connet to steam! The problem might be with your connection, or the server is down.");
            return;
        } catch(ConnectException c) {
            System.out.println("Could not connet to steam! The problem might be with your connection, or the server is down.");
        } catch (IOException i) {
            i.printStackTrace();
        }

    }
}
