package main.iotests;

import fakeentities.FakeIOServer;
import main.Cloud2Server;
import org.junit.jupiter.api.*;
import utils.Utils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Общие тесты для всего сервера")
public class TestCloud2IOServer {

    private static Cloud2Server testServer;
    private static File testStorage;

    @BeforeAll
    public static void setUp() {
        testServer = FakeIOServer.getInstance();
        testServer.init();
        testStorage = testServer.getStorage();
    }

    @Test
    @DisplayName("Проверка существования директории хранилища")
    public void testIsMainStorageExist() {
        makeTestStorage();
        Assertions.assertTrue(testServer.isMainStorageExist());
    }

    private void makeTestStorage() {
        if (!testStorage.exists()) {
            testStorage.mkdir();
        }
    }

    @Test
    @DisplayName("Проверка отсутствия директории хранилища")
    public void testIsMainStorageNotExist() {
        removeTestStorage();
        Assertions.assertFalse(testServer.isMainStorageExist());
    }

    private void removeTestStorage() {
        if (testStorage.exists()) {
            Utils.recursiveDelete(testStorage);
        }
    }

    @Test
    @DisplayName("Проверка создания директории")
    public void testCreateMainStorage() {
        removeTestStorage();
        try {
            testServer.setUpMainStorage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(testServer.isMainStorageExist());
    }

}