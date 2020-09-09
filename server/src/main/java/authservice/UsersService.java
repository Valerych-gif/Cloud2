package authservice;

import entities.User;
import network.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import shareservice.ShareService;
import utils.LogUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UsersService {

    public final static Path AUTH_FILE_PATH = Paths.get(
            Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
            Cloud2ServerSettings.AUTH_FILE
            );

    private AuthorisationService authorisationService;
    private RegistrationService registrationService;
    private ShareService shareService;

    private static UsersService instance;

    private Logger logger = LogManager.getLogger(UsersService.class);

    private UsersService(Network network) {
        this.authorisationService = new AuthorisationService(network);
        this.registrationService = new RegistrationService(network);
        this.shareService = new ShareService();
        LogUtils.info("Authorization service started successfully", logger);
    }

    public static UsersService getInstance(Network network) {
        if (instance == null) {
            instance = new UsersService(network);
        }
        return instance;
    }

    public User getUserByLoginAndPass() {
        return authorisationService.getUserByLoginAndPasswordFromClient();
    }

    public User getNewUserByLoginAndPass() {
        return registrationService.createNewUserByLoginAndPassFromClient();
    }

    public void shareFile() {
        shareService.shareFileByCommandFromClient();
    }
}
