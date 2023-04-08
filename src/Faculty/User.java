package Faculty;

import Application.PasswordEncryptionService;

public sealed class User permits Admin, Teacher, Student{
    protected String email;
    protected String encryptedPassword;

    public User(String email, String encryptedPassword){
        this.email = email;
        this.encryptedPassword = encryptedPassword;
    }

    public void init() { };

    public boolean signIn(String email, String password) {
        String encryptedPassword = PasswordEncryptionService.encryptPassword(password);
        return this.email.equals(email) && this.encryptedPassword.equals(encryptedPassword);
    }

    public String getEmail() {
        return email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

}
