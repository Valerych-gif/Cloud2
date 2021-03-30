package ru.valerych.cloud2.authservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.UserCantBeAuthorized;
import ru.valerych.cloud2.exceptions.UserNotFoundException;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class IOAuthorisationServiceTest {

    private static User user;

    @BeforeAll
    static void setUpUser(){
        user = new User(0, "test", "test");
        user.setUpUser(Paths.get("../storage/0"));
    }

    @Test
    @DisplayName("Getting User by login and pass is success")
    void getUserByLoginAndPasswordSuccessTest() throws UserNotFoundException, UserCantBeAuthorized {
        IOAuthorisationService authorisationService = new IOAuthorisationService();
        User authUser = authorisationService.getUserByLoginAndPassword("test", "test");
        Assertions.assertEquals(0, authUser.getId());
    }

    @Test
    @DisplayName("Getting User by login and pass is fail. Login isn't exist")
    void getUserByLoginAndPasswordUserNotFoundException() {
        IOAuthorisationService authorisationService = new IOAuthorisationService();
        Assertions.assertThrows(UserNotFoundException.class, ()->authorisationService.getUserByLoginAndPassword("bad", "test"));
    }

    @Test
    @DisplayName("Getting User by login and pass is fail. Password is incorrect")
    void getUserByLoginAndPasswordUserCantBeAuthorized() {
        IOAuthorisationService authorisationService = new IOAuthorisationService();
        Assertions.assertThrows(UserCantBeAuthorized.class, ()->authorisationService.getUserByLoginAndPassword("test", "bad"));
    }
}