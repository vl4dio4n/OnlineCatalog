package Application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class AuditService {
    private static AuditService auditService;
    private static final String filePath = "resources/audit.csv";

    private AuditService() {}

    public static AuditService getInstance(){
        if(auditService == null)
            auditService = new AuditService();
        return auditService;
    }

    public void logAction(String action){
        try{
            Files.write(Paths.get(filePath), (action + "," + LocalDateTime.now() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}
    }
}
