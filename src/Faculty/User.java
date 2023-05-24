package Faculty;

import Application.PasswordEncryptionService;
import DataUtils.UsersService;

import java.sql.Connection;

public sealed class User permits Admin, Teacher, Student{

    protected int userId;
    protected String username;
    protected String encryptedPassword;


    public User(int userId, String username, String encryptedPassword){
        this.userId = userId;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public void init() { };

    public int getUserId() { return userId; }

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

}
