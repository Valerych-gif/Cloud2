package main.iotests;

import fakeentities.FakeIOServer;
import io.IOConnectionHandler;
import fakeentities.FakeClient;
import main.Cloud2Server;
import org.junit.jupiter.api.*;
import utils.Utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Общие тесты для всего сервера")
public class TestCloud2IOServer {

    private Cloud2Server testServer;
    private File testStorage;
    private FakeClient client;
    private IOConnectionHandler connectionHandler;
    private DataInputStream is;


    @BeforeEach
    public void setUp() {
        testServer = FakeIOServer.getInstance();
        testServer.init();
        testStorage = testServer.getStorage();
        client = new FakeClient();
        client.connect();
        testServer.waitConnection();
        connectionHandler = testServer.getConnectionHandler();
    }

    @AfterEach
    public void tearDownTest() {
        ((FakeIOServer)testServer).closeConnection();
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


    @Test
    @DisplayName("Проверка, что мы получили экземпляр ConnectionHandler")
    public void testConnectionHandlerIsNotNull() {
        Assertions.assertNotNull(connectionHandler);
    }

    @Test
    @DisplayName("Закрытие соединения со стороны сервера")
    public void testCloseConnectionByCommandFromClient() {

        client.sendCommand("./closeconnection");
        connectionHandler.run();
        Exception socketException = assertThrows(SocketException.class, () ->connectionHandler.getDataInputStream().readUTF());
        Assertions.assertEquals("socket closed", socketException.getMessage());
    }
}