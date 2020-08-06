package main;

import io.Cloud2IOServer;

public class Cloud2ServerStarter {

    public final static String STORAGE_ROOT_DIR = "server/src/main/resources/storage";
    public final static int PORT = 8189;

    public final static int BUFFER_SIZE = 1024;
    public final static char END_COMMAND_CHAR = '|';

    public static void main(String[] args) {
        Cloud2Server server = Cloud2IOServer.getInstance();
        server.init();
        server.waitConnection();
    }

}
