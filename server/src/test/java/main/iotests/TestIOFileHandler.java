package main.iotests;

import fakeentities.FakeClient;
import fakeentities.FakeIOServer;
import files.CloudFile;
import main.FileHandler;
import io.IOFileHandler;
import io.IOConnectionHandler;
import main.ConnectionHandler;
import main.Cloud2Server;
import org.junit.jupiter.api.*;
import utils.Utils;

import java.io.File;
import java.io.IOException;

@DisplayName("Тесты класса управления файлами")
public class TestIOFileHandler {

    private Cloud2Server testServer;
    private ConnectionHandler connectionHandler;
    private FakeClient client;
    private FileHandler fileHandler;

    private File file1;
    private File file2;
    private CloudFile cloudFile1;
    private CloudFile cloudFile2;

    @BeforeEach
    public void setUpTest(){
        testServer = FakeIOServer.getInstance();
        client = new FakeClient();
        client.connect();
        testServer.waitConnection();
        connectionHandler = testServer.getConnectionHandler();
        fileHandler = new IOFileHandler(connectionHandler);

        file1 = new File(client.getFile1().getAbsolutePath());
        file2 = new File(client.getFile2().getAbsolutePath());
        cloudFile1 = new CloudFile(file1.getAbsolutePath(), file1.length());
        cloudFile2 = new CloudFile(file2.getAbsolutePath(), file2.length());
    }

    @AfterEach
    public void tearsDownTest(){
        ((IOConnectionHandler)connectionHandler).closeConnection();
    }

    @Test
    @DisplayName("Помещение файла в хранилище")
    public void testPutFileIntoStorage() throws IOException {
        Utils.clearStorage(testServer.getStorage());
        client.sendFiles();
        connectionHandler.run();
        String file1Path = testServer.getStorage().toString() + "/testFile1.txt";
        File file1 = new File(file1Path);
        String file2Path = testServer.getStorage().toString() + "/testFile2.txt";
        File file2 = new File(file2Path);
        Assertions.assertTrue(file1.exists());
        Assertions.assertTrue(file2.exists());
    }
}
