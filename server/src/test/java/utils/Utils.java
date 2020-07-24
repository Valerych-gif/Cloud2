package utils;

import fakeantities.FakeClient;
import main.Cloud2IOServer;
import main.Cloud2Server;
import main.Cloud2ServerStarter;
import main.ConnectionHandler;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Utils {

    public static void clearStorage(File testStorage) {
        Utils.recursiveDelete(testStorage);
        if (!testStorage.exists()){
            testStorage.mkdir();
        }
    }

    public static void recursiveDelete(File file) {
        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }

        file.delete();
    }
}
