package Application;

import DataUtils.UsersService;
import Faculty.User;

public sealed class AppService permits AppServiceTeacher, AppServiceAdmin, AppServiceStudent {

    public static User authenticate(String username, String password){
        UsersService usersService = UsersService.getUsersService(null);
        return usersService.getUser(username, PasswordEncryptionService.encryptPassword(password));
    }

}
