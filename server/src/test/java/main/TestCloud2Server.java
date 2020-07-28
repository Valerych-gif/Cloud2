package main;

import fakeantities.FakeClient;
import fakeantities.FakeServer;
import jdk.nashorn.internal.ir.annotations.Ignore;
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
public class TestCloud2Server {

    private Cloud2Server testServer;
    private File testStorage;
    private FakeClient client;
    private ConnectionHandler connectionHandler;
    private DataInputStream is;


    @BeforeEach
    public void setUp() {
        testServer = FakeServer.getInstance();
        testServer.init();
        testStorage = testServer.getStorage();
        client = new FakeClient();
        client.connect();
        testServer.waitConnection();
        connectionHandler = testServer.getConnectionHandler();
    }

    @AfterEach
    public void tearDownTest() {
        ((FakeServer)testServer).closeConnection();
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
    @DisplayName("Проверка полученения команды от клиента")
    public void testClientCommandReceive() throws IOException {
        String command = "./somecommand";
        client.sendCommand(command);
        is = connectionHandler.getDataInputStream();
        String commandText = is.readUTF();
        assertEquals(command, commandText);
    }

    @Test
    @DisplayName("Закрытие соединения со стороны сервера")
    public void testCloseConnectionByCommandFromClient() {

        client.sendCommand("./close");
        connectionHandler.run();
        System.out.println(connectionHandler.getDataInputStream());
        Exception socketException = assertThrows(SocketException.class, () ->connectionHandler.getDataInputStream().readUTF());
        Assertions.assertEquals("socket closed", socketException.getMessage());
    }
}