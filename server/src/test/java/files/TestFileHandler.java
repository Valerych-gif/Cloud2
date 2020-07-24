package files;

import fakeantities.FakeClient;
import main.Cloud2Server;
import main.ConnectionHandler;
import org.junit.jupiter.api.*;
import utils.Utils;

import java.io.File;
import java.io.IOException;

@Disabled
@DisplayName("Тесты класса управления файлами")
public class TestFileHandler {

    public final static String TEST_STORAGE_ROOT_DIR = "src/test/resources/storage";

    private static Cloud2Server testServer;

    private static File file1;
    private static File file2;
    private static CloudFile cloudFile1;
    private static CloudFile cloudFile2;
    private static ConnectionHandler connectionHandler;
    private static FileHandler fileHandler;
    private static FakeClient client;

    @BeforeAll
    public static void setUp(){

    }

    @BeforeEach
    public void setUpTest(){
        client = new FakeClient();
        connectionHandler = Utils.getConnectionHandler(client);
        file1 = new File(client.getFile1().getAbsolutePath());
        file2 = new File(client.getFile2().getAbsolutePath());
        cloudFile1 = new CloudFile(file1.getAbsolutePath(), file1.length());
        cloudFile2 = new CloudFile(file2.getAbsolutePath(), file2.length());
        fileHandler = new FileHandler(connectionHandler);
    }

    @AfterEach
    public void tearsDownTest(){
        connectionHandler = Utils.killConnectionHandler(client);
        client = null;
    }

    @Disabled
    @Test
    @DisplayName("Помещение файла в хранилище")
    public void testPutFileIntoStorage() throws IOException {
        clearStorage();
        client.sendFiles();
        Assertions.assertTrue(fileHandler.loadFileToStorage(cloudFile1));
        Assertions.assertTrue(fileHandler.loadFileToStorage(cloudFile2));
    }

    private void clearStorage() {
        File testStorage = new File(TEST_STORAGE_ROOT_DIR);
        Utils.recursiveDelete(testStorage);
        if (!testStorage.exists()){
            testStorage.mkdir();
        }
    }
}
