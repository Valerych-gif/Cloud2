import exceptions.CantToCreateStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cloud2Server {

    private ServerSocket server;
    private ExecutorService executor;
    private Socket socket;
    private File storage = new File(Cloud2ServerStarter.STORAGE_ROOT_DIR);
    private Logger logger = LogManager.getLogger(Cloud2ServerStarter.class);
    private static Cloud2Server instance;

    private Cloud2Server() {
    }

    public static Cloud2Server getInstance() {
        if (instance == null) {
            instance = new Cloud2Server();
        }
        return instance;
    }

    public void init() {
        try {
            setUpMainStorage();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void waitConnection() {
        try {
            server = new ServerSocket(Cloud2ServerStarter.PORT);
            executor = Executors.newCachedThreadPool();
            logger.info("Server started.");
            while (true) {
                socket = server.accept();
                executor.execute(new ConnectionHandler(socket));
                logger.info("Client connected");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void setUpMainStorage() throws Exception {
        if (isMainStorageExist()) return;
        if (storage.mkdir()) {
            logger.info("Создана корневая папка сетевого хранилища");
        } else {
            throw new CantToCreateStorageException();
        }
    }

    public boolean isMainStorageExist() {
        return storage.exists();
    }

    public void setTestStorage(File storage) {
        this.storage = storage;
    }
}
