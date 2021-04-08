package ru.valerych.cloud2.client.network;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.exceptions.LoginUnsuccessfulException;
import ru.valerych.cloud2.client.utils.Utils;
import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class ConnectionHandlerTest {

    private final String LOGIN = "login";
    private final String PASSWORD = "password";

    private volatile Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private volatile ConnectionHandler connectionHandler;

    @BeforeEach
    void init() throws IOException {
        int tmpPort = Utils.getPort();
        new Thread(() -> {
            connectionHandler = new ConnectionHandler();
            try {
                connectionHandler.connectToServer("localhost", String.valueOf(tmpPort));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        while (connectionHandler == null) ;
        final ServerSocket serverSocket = new ServerSocket(tmpPort);
        socket = new Socket();
        socket = serverSocket.accept();
        while (socket == null) ;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @AfterEach
    void closeConnection() {
        try {
            if (socket!=null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Connection to server is success")
    void connectToServerSuccessTest() {
        Assertions.assertNotNull(socket);
    }

    @Test
    void loginToServerSuccessTest() throws IOException {

        new Thread(() -> {
            try {
                connectionHandler.loginToServer(LOGIN, PASSWORD);
            } catch (LoginUnsuccessfulException | BadResponseException e) {
                e.printStackTrace();
            }
        }).start();

        Map<String, String> params = serverAuthProcess(inputStream, outputStream);
        Assertions.assertEquals(Requests.AUTHORIZATION.get(), Byte.valueOf(params.get("signalByte")));
        Assertions.assertEquals(LOGIN, params.get("login"));
        Assertions.assertEquals(PASSWORD, params.get("password"));
    }

    @Test
    void loginToServerLoginUnsuccessfulException() {

        new Thread(() -> {
            try {
                serverAuthProcess(inputStream, outputStream);
                outputStream.write(Responses.FAIL.getSignalByte());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Assertions.assertThrows(LoginUnsuccessfulException.class, () -> connectionHandler.loginToServer(LOGIN, PASSWORD));
    }

    @Test
    void registrationToServer() {
    }

    @Test
    void disconnect() {
    }

    private Map<String, String> serverAuthProcess(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {

        Map<String, String> params = new HashMap<>();
        byte signalByte = inputStream.readByte();
        params.put("signalByte", String.valueOf(signalByte));
        outputStream.write(Responses.OK.getSignalByte());

        byte loginLength = inputStream.readByte();
        params.put("loginLength", String.valueOf(loginLength));
        outputStream.write(Responses.OK.getSignalByte());

        byte[] loginBytes = readBytesFromClient(inputStream, loginLength);
        String login = new String(loginBytes);
        params.put("login", login);
        outputStream.write(Responses.OK.getSignalByte());

        byte passwordLength = inputStream.readByte();
        params.put("passwordLength", String.valueOf(passwordLength));
        outputStream.write(Responses.OK.getSignalByte());

        byte[] passwordBytes = readBytesFromClient(inputStream, passwordLength);
        String password = new String(passwordBytes);
        params.put("password", password);
        outputStream.write(Responses.OK.getSignalByte());
        return params;
    }

    private byte[] readBytesFromClient(DataInputStream is, int length) {
        byte[] buffer = new byte[length];
        try {
            for (int i = 0; i < length; i++) {
                buffer[i] = is.readByte();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}