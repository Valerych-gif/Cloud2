package main;

import io.Cloud2IOServer;
import nio.Cloud2NIOServer;

public class Cloud2ServerStarter {

    public static String storageRootDir = "server/src/main/resources/storage";
    public final static int PORT = 8189;

    public final static int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        Cloud2Server server = Cloud2NIOServer.getInstance();
        server.init();
        server.waitConnection();
    }

}
