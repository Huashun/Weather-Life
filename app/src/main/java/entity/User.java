package entity;

/**
 * Created by liangchenzhou on 16/5/6.
 *
 * User Entity uses for user data operation
 */
public class User {
    private int userId;
    private String userNameEmail;
    private String password;

    public static final String CREATETABLESTRING = "CREATE TABLE USERS " +
            "( USER_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "USERNAME TEXT NOT NULL," +
            "PASSWORDS TEXT NOT NULL)";

    public User(){
    }

    public User(int userId, String userNameEmail, String password) {
        this.userId = userId;
        this.userNameEmail = userNameEmail;
        this.password = password;
    }

    public User(String userNameEmail, String password) {
        this.userNameEmail = userNameEmail;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserNameEmail() {
        return userNameEmail;
    }

    public void setUserNameEmail(String userNameEmail) {
        this.userNameEmail = userNameEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
