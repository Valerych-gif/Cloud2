package io;

import main.Cloud2Server;
import main.Cloud2ServerApp;
import settings.Cloud2ServerSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cloud2IOServer extends Cloud2Server{

    private static Cloud2Server instance;

    private static ServerSocket serverSocket;
    private static ExecutorService executor;

    private Cloud2IOServer() {
        super();
    }

    public static Cloud2Server getInstance() {
        if (instance == null) {
            instance = new Cloud2IOServer();
            try {
                serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            } catch (IOException e) {
                logger.error(e);
            }
            executor = Executors.newCachedThreadPool();
        }
        return instance;
    }

    public void waitConnection() {
        logger.info("Server started.");
        try {
            while (true) {
                socket = serverSocket.accept();
                executor.execute(new IOConnectionHandler(socket));
                logger.info("Client connected");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
