package Client;

import java.util.UUID;

import org.json.JSONObject;

public class RequestGenerator {                                                             

    public static JSONObject registerUser(String username, String password, String birthString) {
        JSONObject request = new JSONObject();
        JSONObject requestBody = new JSONObject();
        request.put("requestType", "registerUser");

        requestBody.put("username", username);
        requestBody.put("password", password);
        requestBody.put("birthString", birthString);

        request.put("requestBody", requestBody);
        
        return request;
    }

    public static JSONObject doesUsernameExist(String username) {
        JSONObject request = new JSONObject();
        JSONObject requestBody = new JSONObject();
        request.put("requestType", "doesUsernameExist");

        requestBody.put("username", username);

        request.put("requestBody", requestBody);
        
        return request;
    }

    public static JSONObject logIn(String username, String password) {
        JSONObject request = new JSONObject();
        JSONObject requestBody = new JSONObject();
        request.put("requestType", "logIn");

        requestBody.put("username", username);
        requestBody.put("password", password);

        request.put("requestBody", requestBody);
        
        return request;
    }

    public static JSONObject getAllGames() {
        JSONObject request = new JSONObject();
        JSONObject requestBody = new JSONObject();
        request.put("requestType", "gamesList");

        request.put("requestBody", requestBody);
        
        return request;
    }

    public static JSONObject downloadGame(String gameID, UUID userID) {
        JSONObject request = new JSONObject();
        JSONObject requestBody = new JSONObject();
        request.put("requestType", "downloadGame");

        requestBody.put("gameID", gameID);
        requestBody.put("userID", userID.toString());

        request.put("requestBody", requestBody);
        
        return request;
    }
}
