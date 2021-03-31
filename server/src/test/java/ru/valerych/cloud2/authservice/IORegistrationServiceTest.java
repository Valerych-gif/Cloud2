package ru.valerych.cloud2.authservice;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.authservice.interfaces.RegistrationService;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.LoginIsNotFreeException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    void getNewUserByLoginAndPasswordSuccessTest() throws LoginIsNotFreeException {
        RegistrationService registrationService = new IORegistrationService();
        User user = registrationService.getNewUserByLoginAndPassword("testuser", "password");
        Assertions.assertEquals("testuser", user.getLogin());
    }

    @Test
    @DisplayName("Getting few new Users by login and password is success")
    void getFewNewUsersByLoginAndPasswordSuccessTest() throws LoginIsNotFreeException {
        RegistrationService registrationService = new IORegistrationService();
        User user1 = registrationService.getNewUserByLoginAndPassword("testuser1", "password");
        User user2 = registrationService.getNewUserByLoginAndPassword("testuser2", "password");
        User user3 = registrationService.getNewUserByLoginAndPassword("testuser3", "password");
        User user4 = registrationService.getNewUserByLoginAndPassword("testuser4", "password");
        Assertions.assertEquals("testuser1", user1.getLogin());
        Assertions.assertEquals("testuser2", user2.getLogin());
        Assertions.assertEquals("testuser3", user3.getLogin());
        Assertions.assertEquals("testuser4", user4.getLogin());
        Assertions.assertEquals(4, user4.getId());
    }

    @Test
    @DisplayName("Getting new User by login and password is fail. Login already in use")
    void getNewUserByLoginAndPasswordLoginIsNotFreeException() throws IOException {
        RegistrationService registrationService = new IORegistrationService();
        Files.write(IOUsersService.AUTH_FILE_PATH,("0 testuser password").getBytes(StandardCharsets.UTF_8));
        Assertions.assertThrows(LoginIsNotFreeException.class, () -> registrationService.getNewUserByLoginAndPassword("testuser", "password"));
    }
}