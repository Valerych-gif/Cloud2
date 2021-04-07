package ru.valerych.cloud2.client.network;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.exceptions.LoginUnsuccessfulException;
import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class ConnectionHandlerTest {

    private final String LOGIN = "login";
    private final String PASSWORD = "password";

    private Socket[] socket;
    private ServerSocket serverSocket;

    @BeforeEach
    void initSocket() throws IOException {
        socket = new Socket[1];
        serverSocket = new ServerSocket(8189);
        new Thread(() -> {
            try {
                socket[0] = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @AfterEach
    void closeConnection() {
        try {
            socket[0].close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Connection to server is success")
    void connectToServerSuccessTest() throws IOException {
        ConnectionHandler connectionHandler = new ConnectionHandler();
        connectionHandler.connectToServer("localhost", "8189");
        Assertions.assertNotNull(socket[0]);
    }

    @Test
    void loginToServerSuccessTest() throws IOException {
        ConnectionHandler connectionHandler = new ConnectionHandler();
        connectionHandler.connectToServer("localhost", "8189");
        DataInputStream inputStream = new DataInputStream(socket[0].getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket[0].getOutputStream());

        new Thread(()-> {
            try {
                connectionHandler.loginToServer(LOGIN, PASSWORD);
            } catch (LoginUnsuccessfulException | BadResponseException e) {
                e.printStackTrace();
            }
        }).start();

        byte signalByte = inputStream.readByte();
        outputStream.write(Responses.OK.getSignalByte());

        byte loginLength = inputStream.readByte();
        outputStream.write(Responses.OK.getSignalByte());

        byte[] loginBytes = readBytesFromClient(inputStream, loginLength);
        String login = new String(loginBytes);
        outputStream.write(Responses.OK.getSignalByte());

        byte passwordLength = inputStream.readByte();
        outputStream.write(Responses.OK.getSignalByte());

        byte[] passwordBytes = readBytesFromClient(inputStream, passwordLength);
        String password = new String(passwordBytes);
        outputStream.write(Responses.OK.getSignalByte());
        outputStream.write(Responses.OK.getSignalByte());

        Assertions.assertEquals(Requests.AUTHORIZATION.get(), signalByte);
        Assertions.assertEquals(LOGIN, login);
        Assertions.assertEquals(PASSWORD, password);
    }

    @Test
    void loginToServerLoginUnsuccessfulException() throws IOException {
        ConnectionHandler connectionHandler = new ConnectionHandler();
        connectionHandler.connectToServer("localhost", "8189");
        DataInputStream inputStream = new DataInputStream(socket[0].getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket[0].getOutputStream());
        new Thread(() -> {
            try {
                authProcess(inputStream, outputStream);
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

    private Map<String, String> authProcess(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {

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