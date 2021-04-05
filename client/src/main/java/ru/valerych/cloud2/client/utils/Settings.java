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

    public static void write(String key, String value) throws IOException {
        createSettingsFileIfNotExist();
        Map<String, String> settings = getAllParams();
        settings.put(key, value);
        reWriteSettings(settings);
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

    public static String read(String key) throws IOException {
        createSettingsFileIfNotExist();
        return getAllParams().get(key);
    }

    public static Map<String, String> getAllParams() throws IOException {
        Map<String, String> settings = new HashMap<>();
        Files.readAllLines(Paths.get(SETTINGS_FILE)).stream()
                .map(s -> s.split(":"))
                .forEach(string -> settings.put(string[0], string[1]));
        return settings;
    }

    private static void createSettingsFileIfNotExist() throws IOException {
        try {
            Files.createFile(Paths.get(SETTINGS_FILE));
        } catch (FileAlreadyExistsException e){
            logger.debug(String.format("File %s already exists", SETTINGS_FILE));
        }
    }
}
