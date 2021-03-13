package ru.valerych.cloud2.servers;

import ru.valerych.cloud2.connectionhandlers.IOConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cloud2IOServer extends Cloud2Server{

    private static final Logger logger = LogManager.getLogger(Cloud2IOServer.class);

    private static ServerSocket serverSocket;
    private static ExecutorService executor;

    public Cloud2IOServer() {
        super();
        executor = Executors.newFixedThreadPool(100);
        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void start() {
        logger.info("Server started.");
        try {
            while (true) {
                logger.info("Server ready for new client connection");
                Socket socket = serverSocket.accept();
                executor.execute(new IOConnectionHandler(socket));
                logger.info("Client connected");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
