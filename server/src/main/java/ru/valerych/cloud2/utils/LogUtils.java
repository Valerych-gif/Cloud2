package ru.valerych.cloud2.utils;

import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

public class LogUtils {

    public static void error(String logString, Logger logger, String prefix){
        printLogIntoConsoleInDebugMode(prefix + logString);
        logger.error(logString);
    }

    public static void error(String logString, Logger logger){
        error(logString, logger, "");
    }

    public static void info(String logString, Logger logger, String prefix){
        printLogIntoConsoleInDebugMode(prefix + logString);
        logger.info(logString);
    }

    public static void info(String logString, Logger logger){
        info(logString, logger, "");
    }

    private static void printLogIntoConsoleInDebugMode(String logString) {
        if (Cloud2ServerSettings.DEBUG_MODE_ENABLED) {
            System.out.println(logString);
        }
    }
}
