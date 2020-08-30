package main;

import servers.Cloud2Server;

public class Cloud2ServerApp {
    public static void main(String[] args) {
        Cloud2Server.getInstance().start();
    }
}
