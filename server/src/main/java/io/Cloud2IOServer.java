package io;

import main.Cloud2Server;
import main.Cloud2ServerStarter;

import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class Cloud2IOServer extends Cloud2Server{

    private static Cloud2Server instance;

    private Cloud2IOServer() {
    }

    public static Cloud2Server getInstance() {
        if (instance == null) {
            instance = new Cloud2IOServer();
        }
        return instance;
    }

    public void waitConnection() {
        try {
            serverSocket = new ServerSocket(Cloud2ServerStarter.PORT);
            executor = Executors.newCachedThreadPool();
            logger.info("Server started.");
            while (true) {
                socket = serverSocket.accept();
                executor.execute(new ConnectionHandler(this, socket));
                logger.info("Client connected");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
