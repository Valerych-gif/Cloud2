package ru.valerych.cloud2.authservice;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.authservice.interfaces.RegistrationService;
import ru.valerych.cloud2.authservice.interfaces.UsersService;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.LoginIsNotFreeException;
import ru.valerych.cloud2.exceptions.UserCantBeAuthorized;
import ru.valerych.cloud2.exceptions.UserNotFoundException;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.utils.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

class IOUsersServiceTest {

    private static Network network;
    private static volatile Client client;
    private static ServerSocket serverSocket;

    private final String LOGIN = "test";
    private final String PASSWORD = "test";
    private final String INVALID_PARAM = "bad";

    @BeforeAll
    static void init() {

        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            new Thread(() -> client = new Client()).start();
            while (client == null) ;
            Socket socket = serverSocket.accept();
            NetworkFactory networkFactory = new IONetworkFactory();
            network = networkFactory.createNetwork(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void clearAuthFile() throws IOException {
        Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
        Files.createFile(IOUsersService.AUTH_FILE_PATH);
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
        try {
            serverSocket.close();
            network.closeConnection();
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Authorization first user in list by login and password is success")
    void authUserByLoginAndPasswordSuccessTest() throws IOException, UserNotFoundException, UserCantBeAuthorized {
        fillAuthDB();
        UsersService usersService = new IOUsersService(network);
        byte[] loginLengthBytes = new byte[]{(byte) (LOGIN + "1").length()};
        client.sendBytesToServer(loginLengthBytes);
        client.sendBytesToServer((LOGIN + "1").getBytes(StandardCharsets.UTF_8));
        byte[] passwordLengthBytes = new byte[]{(byte) PASSWORD.length()};
        client.sendBytesToServer(passwordLengthBytes);
        client.sendBytesToServer(PASSWORD.getBytes(StandardCharsets.UTF_8));
        User user = usersService.authUserByLoginAndPassword();
        Assertions.assertEquals((LOGIN + "1"), user.getLogin());
    }

    @Test
    @DisplayName("Authorization last user in list by login and password is success")
    void authUserByLoginAndPasswordLastUserSuccessTest() throws IOException, UserNotFoundException, UserCantBeAuthorized {
        fillAuthDB();
        UsersService usersService = new IOUsersService(network);
        byte[] loginLengthBytes = new byte[]{(byte) (LOGIN + "4").length()};
        client.sendBytesToServer(loginLengthBytes);
        client.sendBytesToServer((LOGIN + "4").getBytes(StandardCharsets.UTF_8));
        byte[] passwordLengthBytes = new byte[]{(byte) PASSWORD.length()};
        client.sendBytesToServer(passwordLengthBytes);
        client.sendBytesToServer(PASSWORD.getBytes(StandardCharsets.UTF_8));
        User user = usersService.authUserByLoginAndPassword();
        Assertions.assertEquals(LOGIN + "4", user.getLogin());
    }

    @Test
    @DisplayName("Authorization not first and not last user in list by login and password is success")
    void authUserByLoginAndPasswordThirdUserSuccessTest() throws IOException, UserNotFoundException, UserCantBeAuthorized {
        fillAuthDB();
        UsersService usersService = new IOUsersService(network);
        byte[] loginLengthBytes = new byte[]{(byte) (LOGIN + "3").length()};
        client.sendBytesToServer(loginLengthBytes);
        client.sendBytesToServer((LOGIN + "3").getBytes(StandardCharsets.UTF_8));
        byte[] passwordLengthBytes = new byte[]{(byte) PASSWORD.length()};
        client.sendBytesToServer(passwordLengthBytes);
        client.sendBytesToServer(PASSWORD.getBytes(StandardCharsets.UTF_8));
        User user = usersService.authUserByLoginAndPassword();
        Assertions.assertEquals(LOGIN + "3", user.getLogin());
    }

    private void fillAuthDB() throws IOException {
        for (int i = 1; i <= 10; i++) {
            Files.write(IOUsersService.AUTH_FILE_PATH, (i + " " + LOGIN + i + " " + PASSWORD + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        }
    }

    @Test
    @DisplayName("Authorization user by login and password is fail. User not found")
    void authUserByLoginAndPasswordUserNotFoundException() throws IOException {
        Files.write(IOUsersService.AUTH_FILE_PATH, ("0 " + LOGIN + " " + PASSWORD).getBytes(StandardCharsets.UTF_8));
        UsersService usersService = new IOUsersService(network);
        byte[] loginLengthBytes = new byte[]{(byte) INVALID_PARAM.length()};
        client.sendBytesToServer(loginLengthBytes);
        client.sendBytesToServer(INVALID_PARAM.getBytes(StandardCharsets.UTF_8));
        byte[] passwordLengthBytes = new byte[]{(byte) PASSWORD.length()};
        client.sendBytesToServer(passwordLengthBytes);
        client.sendBytesToServer(PASSWORD.getBytes(StandardCharsets.UTF_8));
        Assertions.assertThrows(UserNotFoundException.class, () -> usersService.authUserByLoginAndPassword());
    }

    @Test
    @DisplayName("Registration user by login and password is success")
    void registrationUserByLoginAndPasswordSuccessTest() throws LoginIsNotFreeException {
        UsersService usersService = new IOUsersService(network);
        byte[] loginLengthBytes = new byte[]{(byte) LOGIN.length()};
        client.sendBytesToServer(loginLengthBytes);
        client.sendBytesToServer(LOGIN.getBytes(StandardCharsets.UTF_8));
        byte[] passwordLengthBytes = new byte[]{(byte) PASSWORD.length()};
        client.sendBytesToServer(passwordLengthBytes);
        client.sendBytesToServer(PASSWORD.getBytes(StandardCharsets.UTF_8));
        User user = usersService.registrationUserByLoginAndPassword();
        Assertions.assertEquals(LOGIN, user.getLogin());
    }

    @Test
    @DisplayName("Getting new User by login and password is fail. Login already in use")
    void registrationUserByLoginAndPasswordLoginIsNotFreeException() throws IOException {
        Files.write(IOUsersService.AUTH_FILE_PATH, ("0 " + LOGIN + " " + PASSWORD).getBytes(StandardCharsets.UTF_8));
        UsersService usersService = new IOUsersService(network);
        byte[] loginLengthBytes = new byte[]{(byte) LOGIN.length()};
        client.sendBytesToServer(loginLengthBytes);
        client.sendBytesToServer(LOGIN.getBytes(StandardCharsets.UTF_8));
        byte[] passwordLengthBytes = new byte[]{(byte) PASSWORD.length()};
        client.sendBytesToServer(passwordLengthBytes);
        client.sendBytesToServer(PASSWORD.getBytes(StandardCharsets.UTF_8));
        Assertions.assertThrows(LoginIsNotFreeException.class, () -> usersService.registrationUserByLoginAndPassword());
    }
}