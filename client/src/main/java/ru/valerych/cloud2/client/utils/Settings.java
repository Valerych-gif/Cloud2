package ru.valerych.cloud2.client.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private static final String SETTINGS_FILE = "cloud2client.settings";

    private static final Logger logger = LogManager.getLogger(Settings.class.getName());

    public static void write(String key, String value){
        try {
            createSettingsFileIfNotExist();
            Map<String, String> settings = getAllParams();
            settings.put(key, value);
            reWriteSettings(settings);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static void delete(String key) throws IOException {
        createSettingsFileIfNotExist();
        Map<String, String> settings = getAllParams();
        settings.remove(key);
        reWriteSettings(settings);
    }

    private static void reWriteSettings(Map<String, String> settings) throws IOException {
        Files.delete(Paths.get(SETTINGS_FILE));
        Files.createFile(Paths.get(SETTINGS_FILE));
        settings.forEach((k, v) -> {
            try {
                Files.write(Paths.get(SETTINGS_FILE), (k + ":" + v + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            } catch (IOException e) {
                logger.error(String.format("Can't write to file %s. Cause: %s", SETTINGS_FILE, e));
            }
        });
    }

    public static String read(String key){
        try {
            createSettingsFileIfNotExist();
            return getAllParams().get(key);
        } catch (IOException e) {
            logger.error(e);
        }
        return "";
    }

    public static Map<String, String> getAllParams() {
        Map<String, String> settings = new HashMap<>();
        try {
            Files.readAllLines(Paths.get(SETTINGS_FILE)).stream()
                    .map(s -> s.split(":"))
                    .forEach(string -> settings.put(string[0], string.length>1?string[1]:""));
        } catch (IOException e) {
            logger.error(e);
        }
        return settings;
    }

    private static void createSettingsFileIfNotExist() throws IOException {
        try {
            if (!Files.exists(Paths.get(SETTINGS_FILE)))
                Files.createFile(Paths.get(SETTINGS_FILE));
        } catch (IOException e){
            logger.debug(e);
        }
    }
}
