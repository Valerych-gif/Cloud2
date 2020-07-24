package main;

import fakeclient.FakeClient;
import org.junit.jupiter.api.*;
import utils.Utils;

import java.io.DataInputStream;
import java.io.IOException;

@DisplayName("Тесты ConnectionHandler")
public class TestConnectionHandler {

    public static FakeClient client;
    public static ConnectionHandler connectionHandler;

    @BeforeEach
    public void setUpTest(){
        client = new FakeClient();
        connectionHandler = Utils.getConnectionHandler(client);
    }

    @AfterEach
    public void tearDownTest(){
        connectionHandler = Utils.killConnectionHandler(client);
        client = null;
    }

    @Test
    @DisplayName("Проверка, что мы получили экземпляр ConnectionHandler")
    public void testConnectionHandlerIsNotNull(){
        Assertions.assertNotNull(connectionHandler);
    }

    @Disabled
    @Test
    @DisplayName("Проверка полученения команды от клиента")
    public void testClientCommandReceive() throws IOException {
        client.sendCommand();
        DataInputStream is = connectionHandler.getDataInputStream();System.out.println("commandText");
        String commandText = is.readUTF();

//        Assertions.assertEquals("./somecommand", commandText);
    }

}
