package Client;

import java.util.Date;
import java.util.UUID;

public class User {
    private UUID userID;
    private String username;
    private String birthDate;

    public User(UUID userID, String username, String birthday) {
        this.userID = userID;
        this.username = username;
        this.birthDate = birthday;
    }

    public UUID getUserID() {
        return this.userID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getBirthDate() {
        return this.birthDate;
    }
    
}
