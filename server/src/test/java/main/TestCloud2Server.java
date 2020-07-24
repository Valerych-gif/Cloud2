package main;

import fakeantities.FakeServer;
import main.Cloud2Server;
import org.junit.jupiter.api.*;
import utils.Utils;

import java.io.File;

@DisplayName("Общие тесты для всего сервера")
public class TestCloud2Server {

    private static Cloud2Server testServer;
    private static File testStorage;

    @BeforeAll
    public static void setUp(){
        testServer = FakeServer.getInstance();
        testServer.init();
        testStorage=testServer.getStorage();
    }

    @Test
    @DisplayName("Проверка существования директории хранилища")
    public void testIsMainStorageExist(){
        makeTestStorage();
        Assertions.assertTrue(testServer.isMainStorageExist());
    }

    private void makeTestStorage() {
        if (!testStorage.exists()){
            testStorage.mkdir();
        }
    }

    @Test
    @DisplayName("Проверка отсутствия директории хранилища")
    public void testIsMainStorageNotExist(){
        removeTestStorage();
        Assertions.assertFalse(testServer.isMainStorageExist());
    }

    private void removeTestStorage() {
        if (testStorage.exists()){
            Utils.recursiveDelete(testStorage);
        }
    }

    @Test
    @DisplayName("Проверка создания директории")
    public void testCreateMainStorage(){
        removeTestStorage();
        try {
            testServer.setUpMainStorage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(testServer.isMainStorageExist());
    }

}
