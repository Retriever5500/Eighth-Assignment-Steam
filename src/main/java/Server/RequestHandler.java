package Server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

public class RequestHandler {

    public static JSONObject handleRequest(JSONObject request, DatabaseManager databaseManager, String clientIP) {
         JSONObject response = null;
        boolean result;
        switch(request.getString("requestType")) {
            case "doesUsernameExist":
                System.out.println("Client " + clientIP + " requested doesUsernameExist");
                boolean userExists = doesUsernameExist(request, databaseManager);
                response = ResponseGenerator.doesUsernameExist(userExists);
                break;
            case "gamesList":
                System.out.println("Client " + clientIP + " requested gamesList");
                JSONObject gamesList = getGamesList(databaseManager);
                response = ResponseGenerator.getGamesList(gamesList);
                break;
            case "registerUser":
                System.out.println("Client " + clientIP + " requested registerUser");
                result = registerUser(request, databaseManager);
                response = ResponseGenerator.registerUser(result);
                break;
            case "logIn":
                System.out.println("Client " + clientIP + " requested logIn");
                result = logIn(request, databaseManager);
                Date birthDate = null;
                UUID userID = null;
                if(result) {
                    birthDate = databaseManager.getBirthDate(request.getJSONObject("requestBody").getString("username"));
                    userID = databaseManager.getUserID(request.getJSONObject("requestBody").getString("username"));
                }
                response = ResponseGenerator.logIn(result, userID, birthDate);
                break;
            case "downloadGame":
                System.out.println("Client " + clientIP + " requested downloadGame");
                String gamePath = downloadGame(databaseManager, request);
                response = ResponseGenerator.downloadGame(gamePath);
                break;



        }
        return response;
    }

    public static boolean doesUsernameExist(JSONObject request, DatabaseManager databaseManager) {
        return databaseManager.doesUsernameExist(request.getJSONObject("requestBody").getString("username"));
    }

    public static boolean registerUser(JSONObject request, DatabaseManager databaseManager) {
        String username = request.getJSONObject("requestBody").getString("username");
        String password = request.getJSONObject("requestBody").getString("password");
        String birthString = request.getJSONObject("requestBody").getString("birthString");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
        Date birthDate = null;
        try {
           birthDate = formatter.parse(birthString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean result = databaseManager.registerUser(username, password, birthDate);
        return result;
    }

    public static boolean logIn(JSONObject request, DatabaseManager databaseManager) {
        String username = request.getJSONObject("requestBody").getString("username");
        String password = request.getJSONObject("requestBody").getString("password");

        boolean result = databaseManager.isPasswordCorrect(username, password);
        return result;
    }



    public static JSONObject getGamesList(DatabaseManager databaseManager) {
        return databaseManager.getAllGames();
    }

    public static String downloadGame(DatabaseManager databaseManager, JSONObject request) {
        String userIDString = request.getJSONObject("requestBody").getString("userID");
        UUID userID = UUID.fromString(userIDString);
        String gameID = request.getJSONObject("requestBody").getString("gameID");
        String gamePath = databaseManager.getGamePath(gameID);

        databaseManager.incrementDownloadCount(userID, gameID);
        
    
        
        return gamePath;
    }
    
}