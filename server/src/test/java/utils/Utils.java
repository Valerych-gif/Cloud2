package utils;

import main.Cloud2Server;

import java.io.File;

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
