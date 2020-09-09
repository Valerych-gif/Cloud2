package servers;

import connectionhandlers.IOConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cloud2IOServer extends Cloud2Server{

    protected static Logger logger = LogManager.getLogger(Cloud2IOServer.class);

    private static ServerSocket serverSocket;
    private static ExecutorService executor;

    public Cloud2IOServer() {
        super();
        executor = Executors.newFixedThreadPool(100);
        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
        } catch (IOException e) {
            LogUtils.error(e.getMessage(), logger);
        }
    }

    public void start() {
        LogUtils.info("Server started.", logger);
        try {
            while (true) {
                LogUtils.info("Server ready for new client connection", logger);
                Socket socket = serverSocket.accept();
                executor.execute(new IOConnectionHandler(socket));
                LogUtils.info("Client connected", logger);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), logger);
        }
    }
}
