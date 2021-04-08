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
        while (connectionHandler==null);
        ServerSocket serverSocket = new ServerSocket(tmpPort);
        socket = new Socket();
        socket = serverSocket.accept();
        while (socket == null||connectionHandler.getConnection().getInputStream() == null||connectionHandler.getConnection().getOutputStream()==null);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @AfterEach
    void closeConnection() {
        try {
            if (socket != null) socket.close();
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
    @DisplayName("Log in to server is success")
    void loginToServerSuccessTest() throws IOException {
        outputStream.write(Responses.OK.getSignalByte());
        new Thread(() -> {
            try {
                connectionHandler.loginToServer(LOGIN, PASSWORD);
            } catch (LoginUnsuccessfulException | BadResponseException e) {
                e.printStackTrace();
            }
        }).start();

        Map<String, String> params = serverReceiveLoginAndPasswordProcess(inputStream, outputStream);
        Assertions.assertEquals(Requests.AUTHORIZATION.get(), Byte.valueOf(params.get("signalByte")));
        Assertions.assertEquals(LOGIN, params.get("login"));
        Assertions.assertEquals(PASSWORD, params.get("password"));
    }

    @Test
    @DisplayName("Log in to server is fail. LoginUnsuccessfulException")
    void loginToServerLoginUnsuccessfulException() {

        new Thread(() -> {
            try {
                serverReceiveLoginAndPasswordProcess(inputStream, outputStream);
                outputStream.write(Responses.FAIL.getSignalByte());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Assertions.assertThrows(LoginUnsuccessfulException.class, () -> connectionHandler.loginToServer(LOGIN, PASSWORD));
    }

    @Test
    @DisplayName("Log in to server is fail. BadResponseException")
    void loginToServerBadResponseException() {

        new Thread(() -> {
            try {
                inputStream.readByte();
                outputStream.write(Responses.FAIL.getSignalByte());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Assertions.assertThrows(BadResponseException.class, () -> connectionHandler.loginToServer(LOGIN, PASSWORD));
    }

    @Test
    @DisplayName("Registration is success")
    void registrationToServer() throws IOException {
        outputStream.write(Responses.OK.getSignalByte());
        new Thread(() -> {
            try {
                connectionHandler.registrationToServer(LOGIN, PASSWORD);
            } catch (BadResponseException e) {
                e.printStackTrace();
            }
        }).start();

        Map<String, String> params = serverReceiveLoginAndPasswordProcess(inputStream, outputStream);
        Assertions.assertEquals(Requests.REGISTRATION.get(), Byte.valueOf(params.get("signalByte")));
        Assertions.assertEquals(LOGIN, params.get("login"));
        Assertions.assertEquals(PASSWORD, params.get("password"));
    }

    @Test
    @DisplayName("Registration is fail. BadResponseException")
    void registrationToServerBadResponseException() {

        new Thread(() -> {
            try {
                inputStream.readByte();
                outputStream.write(Responses.FAIL.getSignalByte());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Assertions.assertThrows(BadResponseException.class, () -> connectionHandler.registrationToServer(LOGIN, PASSWORD));
    }

    @Test
    @DisplayName("Disconnection is success")
    void disconnectSuccessTest() throws IOException {
        outputStream.write(Responses.OK.getSignalByte());
        connectionHandler.disconnect();
        Assertions.assertEquals(Requests.CLOSE_CONNECTION.get(), inputStream.readByte());
        Assertions.assertTrue(connectionHandler.getConnection().getSocket().isClosed());
    }

    private Map<String, String> serverReceiveLoginAndPasswordProcess(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {

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