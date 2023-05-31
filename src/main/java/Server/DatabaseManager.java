package Server;

import java.util.Random;
import java.util.UUID;

import java.sql.Statement;
import java.time.ZoneId;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

public class DatabaseManager {
    private Connection connection;
    public DatabaseManager(String url) {
        connect(url);
    }

    private void connect(String url) {
        this.connection = null;
        try {  
            // create a connection to the database  
            this.connection  = DriverManager.getConnection(url);  
              
            System.out.println("Connection to SQLite has been established.");  
              
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
    }

    public boolean doesUsernameExist(String username) {
        String sql = "SELECT * FROM accounts\n";
        sql += "where username='" + username + "';";

        Statement stmt;
        try {
            stmt = this.connection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            while(rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }  
        return false;
    }

    public boolean registerUser(String username, String password, java.util.Date utilDate) {
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        String id = UUID.randomUUID().toString();

        String sql = "INSERT INTO accounts(id,username,password,date_of_birth) VALUES(?,?,?,?)";

        String hashedPassword = sha256(password);

        try {
            PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, username);
            pstmt.setString(3, hashedPassword);
            pstmt.setDate(4, sqlDate);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPasswordCorrect(String username, String password) {
        String sql = "SELECT * FROM accounts\n";
        sql += "where username='" + username + "';";

        
        try {
            Statement stmt  = this.connection.createStatement();
            ResultSet rs  = stmt.executeQuery(sql);
            while (rs.next()) {
                if(rs.getString("password").equals(sha256(password))) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public java.util.Date getBirthDate(String username) {
        String sql = "SELECT date_of_birth FROM accounts\n";
        sql += "where username='" + username + "';";

        try {
            Statement pstmt  = this.connection.createStatement();
            ResultSet rs  = pstmt.executeQuery(sql);
            while (rs.next()) {
                java.sql.Date birthDate = rs.getDate("date_of_birth");
                java.util.Date utilBirthDate = Date.from(birthDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                return utilBirthDate;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public UUID getUserID(String username) {
        String sql = "SELECT id FROM accounts\n";
        sql += "where username='" + username + "';";

        try {
            Statement pstmt  = this.connection.createStatement();
            ResultSet rs  = pstmt.executeQuery(sql);
            while (rs.next()) {
                String userIDString = rs.getString("id");
                UUID userID = UUID.fromString(userIDString);
                return userID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void incrementDownloadCount(UUID userID, String gameID) {
        String sql = "SELECT * FROM downloads\n";
        sql += "where account_id='" + userID.toString() + "'\n";
        sql += "AND\n";
        sql += "game_id='" + gameID + "';";

        try {
            Statement stmt  = this.connection.createStatement();
            ResultSet rs  = stmt.executeQuery(sql);
            while (rs.next()) {
                rs.updateInt("download_count", rs.getInt("download_count") + 1);
                rs.updateRow();
                return;
            }
            String secondSql = "INSERT INTO downloads(account_id,game_id,download_count) VALUES(?,?,?)";
            PreparedStatement pstmt = this.connection.prepareStatement(secondSql);
            pstmt.setString(1, userID.toString());
            pstmt.setString(2, gameID);
            pstmt.setInt(3, 1);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String sha256(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) 
                  hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
           throw new RuntimeException(ex);
        }
    }
    

    public JSONObject getAllGames() {
        String sql = "SELECT * FROM games";

        JSONObject gamesList = new JSONObject();
          
        try {   
            Statement stmt  = this.connection.createStatement();  
            ResultSet rs    = stmt.executeQuery(sql);  
              
            // loop through the result set  
            while (rs.next()) {
                JSONObject gameDetails = new JSONObject();
                gameDetails.put("title", rs.getString("title"));
                gameDetails.put("developer", rs.getString("developer"));
                gameDetails.put("genre", rs.getString("genre"));
                gameDetails.put("price", rs.getDouble("price"));
                gameDetails.put("release_year", rs.getInt("release_year"));
                gameDetails.put("controller_support", rs.getInt("controller_support"));
                gameDetails.put("reviews", rs.getInt("reviews"));
                gameDetails.put("size", rs.getInt("size"));
                gameDetails.put("file_path", rs.getString("file_path"));
                gamesList.put(rs.getString("id"), gameDetails);
            }
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
        return gamesList;
    }

    public String getGamePath(String gameID) {
        String sql = "SELECT ID, file_path FROM games\n";
        sql += "where ID='" + gameID + "';";

        try {
            Statement pstmt  = this.connection.createStatement();
            ResultSet rs  = pstmt.executeQuery(sql);
            while (rs.next()) {
                String gamePath = rs.getString("file_path");
                return gamePath;
            }
            String sqlInsert = "INSERT INTO games(account_id,game_id,down;pad_count) VALUES(?,?,?)";

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
