package main;

import fakeantities.FakeClient;
import fakeantities.FakeServer;
import org.junit.jupiter.api.*;
import utils.Utils;

import java.io.DataInputStream;
import java.io.IOException;

@DisplayName("Тесты ConnectionHandler")
public class TestConnectionHandler {

    private Cloud2Server testServer;
    private FakeClient client;
    private ConnectionHandler connectionHandler;

    @BeforeEach
    public void setUpTest(){
        testServer = FakeServer.getInstance();
        client = new FakeClient();
        client.connect();
        testServer.waitConnection();
        connectionHandler = testServer.getConnectionHandler();
    }

    @AfterEach
    public void tearDownTest(){
        testServer.closeConnection();
    }

    @Test
    @DisplayName("Проверка, что мы получили экземпляр ConnectionHandler")
    public void testConnectionHandlerIsNotNull(){
        Assertions.assertNotNull(connectionHandler);
    }

    @Test
    @DisplayName("Проверка полученения команды от клиента")
    public void testClientCommandReceive() throws IOException {
        String command = "./somecommand";
        client.sendCommand(command);
        DataInputStream is = connectionHandler.getDataInputStream();
        String commandText = is.readUTF();
        Assertions.assertEquals(command, commandText);
    }

}
