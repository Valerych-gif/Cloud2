package utils;

import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;

public class LogUtils {

    public static void error(String logString, Logger logger){
        printLogIntoConsoleInDebugMode(logString);
        logger.error(logString);
    }

    public static void info(String logString, Logger logger){
        printLogIntoConsoleInDebugMode(logString);
        logger.info(logString);
    }

    private static void printLogIntoConsoleInDebugMode(String logString) {
        if (Cloud2ServerSettings.DEBUG_MODE_ENABLED) {
            System.out.println(logString);
        }
    }
}
