package ru.valerych.cloud2.authservice;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.authservice.interfaces.AuthorisationService;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.UserCantBeAuthorized;
import ru.valerych.cloud2.exceptions.UserNotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class IOAuthorisationServiceTest {

    @BeforeAll
    static void init() throws IOException {
        Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
        Files.createFile(IOUsersService.AUTH_FILE_PATH);
        Files.write(IOUsersService.AUTH_FILE_PATH,"0 test test".getBytes(StandardCharsets.UTF_8));
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
    }

    @Test
    @DisplayName("Getting User by login and pass is success")
    void getUserByLoginAndPasswordSuccessTest() throws UserNotFoundException, UserCantBeAuthorized {
        AuthorisationService authorisationService = new IOAuthorisationService();
        User authUser = authorisationService.getUserByLoginAndPassword("test", "test");
        Assertions.assertEquals(0, authUser.getId());
    }

    @Test
    @DisplayName("Getting User by login and pass is fail. Login isn't exist")
    void getUserByLoginAndPasswordUserNotFoundException() {
        AuthorisationService authorisationService = new IOAuthorisationService();
        Assertions.assertThrows(UserNotFoundException.class, ()->authorisationService.getUserByLoginAndPassword("bad", "test"));
    }

    @Test
    @DisplayName("Getting User by login and pass is fail. Password is incorrect")
    void getUserByLoginAndPasswordUserCantBeAuthorized() {
        AuthorisationService authorisationService = new IOAuthorisationService();
        Assertions.assertThrows(UserCantBeAuthorized.class, ()->authorisationService.getUserByLoginAndPassword("test", "bad"));
    }
}