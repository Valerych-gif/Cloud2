package fakeentities;

import servers.Cloud2Server;

import java.io.File;

public class FakeNIOServer extends Cloud2Server {

    private static Cloud2Server instance;

    private FakeNIOServer() {
        storage = new File("src/test/resources/storage");
    }

    public static Cloud2Server getInstance() {
        if (instance == null) {
            instance = new FakeNIOServer();
        }
        return instance;
    }

    @Override
    public void start() {

    }
}
