public class Cloud2ServerStarter {

    public final static String STORAGE_ROOT_DIR = "server/src/main/resources/storage";
    public final static int PORT = 8189;

    public final static int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        Cloud2Server server = Cloud2Server.getInstance();
        server.init();
        server.waitConnection();
    }

}
