package authservice;

import entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import shareservice.ShareService;
import utils.LogUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AuthService {

    public final static Path AUTH_FILE_PATH = Paths.get(
            Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
            Cloud2ServerSettings.AUTH_FILE
            );

    private AuthorisationService authorisationService;
    private RegistrationService registrationService;
    private ShareService shareService;

    private static AuthService instance;

    private Logger logger = LogManager.getLogger(AuthService.class);

    private AuthService() {
        this.authorisationService = new AuthorisationService();
        this.registrationService = new RegistrationService();
        this.shareService = new ShareService();
        LogUtils.info("Сервис авторизации успешно запущен", logger);
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public User getUserByLoginAndPass() {
        return authorisationService.getUserByLoginAndPassFromClient();
    }

    public User getNewUserByLoginAndPass() {
        return registrationService.createNewUserByLoginAndPassFromClient();
    }

    public void shareFile() {
        shareService.shareFileByCommandFromClient();
    }
}
