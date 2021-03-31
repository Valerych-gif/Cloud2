package ru.valerych.cloud2.authservice;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.authservice.interfaces.RegistrationService;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.LoginIsNotFreeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class IORegistrationServiceTest {


    @BeforeEach
    void init() throws IOException {
        Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
        Files.createFile(IOUsersService.AUTH_FILE_PATH);
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
    }

    @Test
    @DisplayName("Getting new User by login and password is success")
    void getNewUserByLoginAndPasswordSuccessTest() {
        RegistrationService registrationService = new IORegistrationService();
        User user = registrationService.getNewUserByLoginAndPassword("testuser", "password");
        Assertions.assertEquals("testuser", user.getLogin());
    }

    @Test
    @DisplayName("Getting new User by login and password is fail. Login already in use")
    void getNewUserByLoginAndPasswordLoginIsNotFreeException() {
        RegistrationService registrationService = new IORegistrationService();
        registrationService.getNewUserByLoginAndPassword("testuser", "password");
        Assertions.assertThrows(LoginIsNotFreeException.class, () -> registrationService.getNewUserByLoginAndPassword("testuser", "password"));
    }
}