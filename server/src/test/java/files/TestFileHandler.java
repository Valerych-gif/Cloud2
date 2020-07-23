package files;

import fakeclient.FakeClient;
import main.Cloud2Server;
import main.Cloud2ServerStarter;
import main.ConnectionHandler;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestFileHandler {

    public final static String TEST_STORAGE_ROOT_DIR = "src/test/resources/storage";

    private static Cloud2Server testServer;

    private static File file1;
    private static File file2;
    private static CloudFile cloudFile1;
    private static CloudFile cloudFile2;
    private static ConnectionHandler connectionHandler;
    private static FileHandler fileHandler;
    private static ServerSocket serverSocket;
    private static Socket socket;

    private static FakeClient client;

    @BeforeAll
    @DisplayName("Тесты класса управления файлами")
    public static void setUp(){
        testServer = Cloud2Server.getInstance();
        Cloud2ServerStarter.setStorageRootDir(TEST_STORAGE_ROOT_DIR);
        testServer.init();
        client = new FakeClient();
        client.connect();

        try {
            serverSocket = new ServerSocket(Cloud2ServerStarter.PORT);
            socket = serverSocket.accept();
            connectionHandler = new ConnectionHandler(socket);

        } catch (Exception e) {
            e.printStackTrace();
        }


        file1 = new File(client.getFile1().getAbsolutePath());
        file2 = new File(client.getFile2().getAbsolutePath());
        cloudFile1 = new CloudFile(file1.getAbsolutePath(), file1.length());
        cloudFile2 = new CloudFile(file2.getAbsolutePath(), file2.length());
        fileHandler = new FileHandler(connectionHandler);
    }

    //@Disabled
    @Test
    @DisplayName("Помещение файла в хранилище")
    public void testPutFileIntoStorage() throws IOException {
        client.sendFiles();
        Assertions.assertTrue(fileHandler.loadFileToStorage(cloudFile1));
        Assertions.assertTrue(fileHandler.loadFileToStorage(cloudFile2));
    }

    private void clearStorage() {
        if (file1.exists()) file1.delete();
        if (file2.exists()) file2.delete();
    }
}
