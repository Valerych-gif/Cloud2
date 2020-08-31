//package main.iotests;
//
//import auth.AuthService;
//import fakeentities.FakeClient;
//import connectionhandlers.ConnectionHandler;
//import servers.Cloud2Server;
//import org.junit.jupiter.api.*;
//import utils.Utils;
//
//import java.io.File;
//import java.util.Map;
//
//@DisplayName("Тесты класса управления файлами")
//public class TestIOConnectionHandler {
//
//    public static final String AUTH_FILE_PATH = "src/test/resources/sfiles/auth.db";
//
//    private static Cloud2Server testServer;
//    private static ConnectionHandler connectionHandler;
//    private static FakeClient client;
//
//    private static Map<String, File> clientFiles;
//
//    public static void setTestServer(Cloud2Server testServer) {
//        TestIOConnectionHandler.testServer = testServer;
//    }
//
//    public static void setConnectionHandler(ConnectionHandler connectionHandler) {
//        TestIOConnectionHandler.connectionHandler = connectionHandler;
//    }
//
//    public static void setClient(FakeClient client) {
//        TestIOConnectionHandler.client = client;
//    }
//
//    @BeforeAll
//    public static void setUp() throws Exception {
//        Utils.startServer();
//        Thread.sleep(2000);
//        Utils.clearStorage(testServer.getStorage());
//        AuthService.setAuthFile(AUTH_FILE_PATH);
//        Utils.createClient();
//        Thread.sleep(1000);
//        clientFiles = client.getClientFiles();
//    }
//
//    @BeforeEach
//    public void setUpTest() {
//    }
//
//    @AfterEach
//    public void tearsDownTest(){
//
//    }
//
//    @AfterAll
//    public static void tearsDown(){
//        client.disconnect();
//        connectionHandler.closeConnection();
//    }
//
//    @Test
//    @DisplayName("Проверка, что мы получили экземпляр ConnectionHandler")
//    public void testConnectionHandlerIsNotNull() {
//        Assertions.assertNotNull(connectionHandler);
//    }
//
//    @Test
//    @DisplayName("Помещение файла в хранилище")
//    public void testPutFileIntoStorage(){
//
//        client.sendFile(clientFiles.get("Text file 1"));
//        client.sendFile(clientFiles.get("Text file 2"));
//
//        String userStoragePath = testServer.getStorage().toString() + "/" + connectionHandler.getUserId();
//        String storageTestFile1Path = userStoragePath + "/" + clientFiles.get("Text file 1").getName();
//        File storageTestFile1 = new File(storageTestFile1Path);
//        String storageTestFile2Path = userStoragePath + "/" + clientFiles.get("Text file 2").getName();
//        File storageTestFile2 = new File(storageTestFile2Path);
//
//        Assertions.assertTrue(storageTestFile1.exists());
//        Assertions.assertTrue(storageTestFile2.exists());
//    }
//
//    @Test
//    @DisplayName("Помещение большого файла в хранилище")
//    @Disabled("Слишком долгий тест")
//    public void testPutBigFileIntoStorage(){
//
//        client.sendFile(clientFiles.get("Big file"));
//
//        String userStoragePath = testServer.getStorage().toString() + "/" + connectionHandler.getUserId();
//        String storageTestBigFilePath = userStoragePath + "/" + clientFiles.get("Big file").getName();
//        File storageTestFile1 = new File(storageTestBigFilePath);
//
//        Assertions.assertTrue(storageTestFile1.exists());
//    }
//}
