package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.json.JSONObject;

public class Menu {

    private static Scanner scanner;

    private static DataInputStream input = null;
    private static DataOutputStream output = null;

    public static void run(DataInputStream input, DataOutputStream output) {
        scanner = new Scanner(System.in);

        Menu.input = input;
        Menu.output = output;

        runMenu();

        scanner.close();
    }

    public static void runMenu() {
        clearConsole();
        System.out.println("Welcome to Steam!");
        refresh();

        boolean running = true;

        while(running) {
            System.out.println("1: Create an account");
            System.out.println("2: Log in");
            System.out.println("0: Exit");
            String option = scanner.nextLine();
            
            refresh();
    
            switch(option) {
                case "1":
                    registerAccount();
                    break;
                case "2":
                    User user = logIn();
                    if(user != null) {
                        userMenu(user);
                    }
                    break;
                case "0":
                    System.out.println("Good Bye!");
                    running = false;
                    try {
                        output.writeUTF("Exit");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("No such option!");
                    break;
            }
    
            refresh();
        }
    }

    public static void registerAccount() {
        String username;
        while(true) {
            System.out.println("Choose a username:");
            username = scanner.nextLine();
            sendRequest(RequestGenerator.doesUsernameExist(username));
    
            JSONObject response = getResponse();
            if(response == null) {
                System.out.println("Something went wrong, returning to main menu");
                refresh();
                return;
            } else {
                boolean userExists = ResponseHandler.doesUsernameExist(response);
                if(userExists) {
                    System.out.println("username already exists! Choose another one.");
                    refresh();
                } else {
                    System.out.println("username is available!");
                    break;
                }
            }

        }


        System.out.println("Choose a password:");
        String password = scanner.nextLine();

        String birthString;
        while(true) {
            System.out.println("Type your birth date(dd-M-yyyy):");
            birthString = scanner.nextLine();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
            try {
                formatter.parse(birthString);
                break;
            } catch (ParseException e) {
                System.out.println("Wrong format!");
            }
        }


        sendRequest(RequestGenerator.registerUser(username, password, birthString));
        JSONObject response = getResponse();
        boolean result = ResponseHandler.registerUser(response);
        if(result) {
            System.out.println("Registered successfully");
        } else {
            System.out.println("Something went wrong, try again later.");
        }

    }

    public static User logIn() {
        System.out.println("Type your username: ");
        String username = scanner.nextLine();

        sendRequest(RequestGenerator.doesUsernameExist(username));
        JSONObject response = getResponse();
        if(response == null) {
            System.out.println("Something went wrong, returning to main menu");
            refresh();
            return null;
        } else {
            boolean userExists = ResponseHandler.doesUsernameExist(response);
            if(!userExists) {
                System.out.println("Username does not exist! Please register");
                return null;
            }
        }

        System.out.println("Type your password: ");
        String password = scanner.nextLine();

        
        sendRequest(RequestGenerator.logIn(username, password));

        JSONObject logInResponse = getResponse();
        JSONObject logInResponseShort = ResponseHandler.logIn(logInResponse);

        if(!logInResponseShort.getBoolean("result")) {
            System.out.println("Wrong password!");
            refresh();
            return null;
        } else {
            System.out.println("Logged in successfully!");
            String birthday = logInResponseShort.getString("birthDate");
            UUID userID = UUID.fromString(logInResponseShort.getString("userID"));
            User user = new User(userID, username, birthday);
            refresh();
            return user;
        }
    }

    public static void userMenu(User user) {
        boolean running = true;
        while(running) {
            System.out.println("Choose an option: ");
            System.out.println("1: List all games");
            System.out.println("2: Managed installed games");
            System.out.println("0: Exit");
            String option = scanner.nextLine();
            switch(option) {
                case "1":
                    browseGames(user);
                    break;

                case "2":
                    manageGames();
    
                case "0":
                    running = false;
                    break;
    
            }
        }

    }

    public static void manageGames() {
        File folder = new File("src/main/java/Client/Downloads");
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> gameFiles = new ArrayList<File>();

        for(File file: listOfFiles) {
            String fileName = file.getName();
            if(fileName.endsWith(".png")) {
                gameFiles.add(file);
            }
        }

        sendRequest(RequestGenerator.getAllGames());
        JSONObject response = getResponse();
        JSONObject allGames = ResponseHandler.getAllGames(response);

        int i = 1;
        System.out.println("Choose a game to manage, or simply type 0 to exit");
        for(File gameFile: gameFiles) {
            String gameID = gameFile.getName().split("\\.")[0];
            String gameTitle = allGames.getJSONObject(gameID).getString("title");
            System.out.println(i + ": " + gameTitle);
            i++;
        }

        String option = scanner.nextLine();
        if(option.equals("0")) {
            return;
        } else {
            int gameIndex = Integer.parseInt(option) - 1;
            File chosenFile = gameFiles.get(gameIndex);
            System.out.println("To delete, type delete. Type anything else to exit.");
            String gameOption = scanner.nextLine();
            if(gameOption.equals("delete")) {
                chosenFile.delete();
                System.out.println("File deleted!");
            }
        }
        refresh();
    }

    public static void browseGames(User user) {
        sendRequest(RequestGenerator.getAllGames());
        JSONObject response = getResponse();
        JSONObject allGames = ResponseHandler.getAllGames(response);

        Iterator<String> keys = allGames.keys();
        ArrayList<String> keysList = new ArrayList<String>();

        while(keys.hasNext()) {
            String key = keys.next();
            keysList.add(key);
        }

        System.out.println("Choose a game or exit with 0:");

        int index = 0;
        for(String key: keysList) {
            System.out.println((index + 1) + ": " + allGames.getJSONObject(key).getString("title"));
            index++;
        }

        String option = scanner.nextLine();
        refresh();
        if(option.equals("0")) {
            return;
        } else {
            int gameIndex = Integer.parseInt(option) - 1;
            String key = keysList.get(gameIndex);
            System.out.println("ID: " + key);
            System.out.println("title: " + allGames.getJSONObject(key).getString("title"));
            System.out.println("developer: " + allGames.getJSONObject(key).getString("developer"));
            System.out.println("genre: " + allGames.getJSONObject(key).getString("genre"));
            System.out.println("price: " + allGames.getJSONObject(key).getDouble("price"));
            System.out.println("release year: " + allGames.getJSONObject(key).getInt("release_year"));
            System.out.println("controller support: " + allGames.getJSONObject(key).getInt("controller_support"));
            System.out.println("reviews: " + allGames.getJSONObject(key).getInt("reviews"));
            System.out.println("size: " + allGames.getJSONObject(key).getInt("size"));

            System.out.println("To download, type download. Type anything else to exit.");
            option = scanner.nextLine();

            if(option.equals("download")) {
                downloadGame(user, key);
            } else {
                return;
            }
        }
    }

    public static void downloadGame(User user, String gameID) {
        sendRequest(RequestGenerator.downloadGame(gameID, user.getUserID()));
        String downloadPath = "src/main/java/Client/" + "Downloads/" + gameID + ".png";
        receiveFile(downloadPath);
    }

    public static void sendRequest(JSONObject request) {
        try {
            output.writeUTF((request.toString()));
            output.flush();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }

    public static JSONObject getResponse() {
        String responseString = null;
        try {
            responseString = input.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(responseString == null) {
            return null;
        } else {
            JSONObject response = new JSONObject(responseString);
            return response;
        }
    }

    private static void receiveFile(String fileName) {
        int bytes = 0;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            
            e.printStackTrace();
        }

        long size = 0;
        try {
            size = input.readLong();
        } catch (IOException e) {
            
            e.printStackTrace();
        } // read file size
        byte[] buffer = new byte[4 * 1024];
        try {
            while (size > 0
                && (bytes = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                // Here we write the file using write method
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes; // read upto file size
            }
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        // Here we received file
        System.out.println("File is Received");
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }

    public static void sleep(int timeSec) {
        try {
            Thread.sleep(timeSec*1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void clearConsole() {
        System.out.print(String.format("\033[H\033[2J"));
    }

    public static void refresh() {
        sleep(2);
        clearConsole();
    }
}
