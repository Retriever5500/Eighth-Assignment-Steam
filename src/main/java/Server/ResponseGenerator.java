package Server;

import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

public class ResponseGenerator {

    public static JSONObject doesUsernameExist(boolean userExists) {
        JSONObject response = new JSONObject();
        JSONObject responseBody = new JSONObject();
        response.put("responseType", "doesUsernameExist");

        responseBody.put("doesUsernameExist", userExists);

        response.put("responseBody", responseBody);
        
        return response;
    }

    public static JSONObject getGamesList(JSONObject gamesList) {
        JSONObject response = new JSONObject();
        JSONObject responseBody = new JSONObject();
        response.put("responseType", "gamesList");

        responseBody.put("gamesList", gamesList);

        response.put("responseBody", responseBody);
        return response;
    }

    public static JSONObject registerUser(boolean result) {
        JSONObject response = new JSONObject();
        JSONObject responseBody = new JSONObject();
        response.put("responseType", "registerUser");

        responseBody.put("result", result);

        response.put("responseBody", responseBody);
        return response;
    }

    public static JSONObject logIn(boolean result, UUID userID, Date birthDate) {
        JSONObject response = new JSONObject();
        JSONObject responseBody = new JSONObject();
        response.put("responseType", "logIn");

        responseBody.put("result", result);
        if(birthDate != null) {
            responseBody.put("birthDate", birthDate.toString());
        }
        responseBody.put("userID", userID.toString());

        response.put("responseBody", responseBody);
        return response;
    }

    public static JSONObject downloadGame(String gamePath) {
        JSONObject response = new JSONObject();
        JSONObject responseBody = new JSONObject();
        response.put("responseType", "downloadGame");

        responseBody.put("gamePath", gamePath);

        response.put("responseBody", responseBody);
        return response;
    }
}
