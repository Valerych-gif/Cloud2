package main;

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

    @Override
    public void closeConnection() {

    }
}
