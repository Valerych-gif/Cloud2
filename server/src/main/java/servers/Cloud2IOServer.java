package servers;

import connectionhandlers.IOConnectionHandler;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cloud2IOServer extends Cloud2Server{

    private static Cloud2Server instance;

    private static ServerSocket serverSocket;
    private static ExecutorService executor;

    public Cloud2IOServer() {
        super();
        executor = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
        } catch (IOException e) {
            LogUtils.error(e.getMessage(), Cloud2Server.logger);
        }
    }

    public void start() {
        LogUtils.info("Server started.", Cloud2Server.logger);
        try {
            while (true) {
                LogUtils.info("Waiting for connection", Cloud2Server.logger);
                Socket socket = serverSocket.accept();
                executor.execute(new IOConnectionHandler(socket));
                LogUtils.info("Client connected", Cloud2Server.logger);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), Cloud2Server.logger);
        }
    }
}
