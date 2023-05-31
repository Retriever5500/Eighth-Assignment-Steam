package Client;

import java.util.Date;

import org.json.JSONObject;

public class ResponseHandler {
    public static boolean doesUsernameExist(JSONObject response) {
        boolean result = response.getJSONObject("responseBody").getBoolean("doesUsernameExist");
        return result;
    }

    public static boolean registerUser(JSONObject response) {
        boolean result = response.getJSONObject("responseBody").getBoolean("result");
        return result;
    }

    public static JSONObject logIn(JSONObject response) {
        JSONObject resultJSON = new JSONObject();
        boolean result = response.getJSONObject("responseBody").getBoolean("result");
        resultJSON.put("result", result);
        if(result) {
            String birthDate = response.getJSONObject("responseBody").getString("birthDate");
            resultJSON.put("birthDate", birthDate);
            String userID = response.getJSONObject("responseBody").getString("userID");
            resultJSON.put("userID", userID);
        }

        return resultJSON;
    }

    public static JSONObject getAllGames(JSONObject response) {
        JSONObject allGames = response.getJSONObject("responseBody").getJSONObject("gamesList");
        return allGames;
    }
}
