package ru.valerych.cloud2.authservice.interfaces;

import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.LoginIsNotFreeException;
import ru.valerych.cloud2.exceptions.UserCantBeAuthorized;
import ru.valerych.cloud2.exceptions.UserNotFoundException;

public interface UsersService {
    User authUserByLoginAndPassword() throws UserNotFoundException, UserCantBeAuthorized;
    User registrationUserByLoginAndPassword() throws LoginIsNotFreeException;
}
