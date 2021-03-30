package ru.valerych.cloud2.authservice.interfaces;

import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.UserCantBeAuthorized;
import ru.valerych.cloud2.exceptions.UserNotFoundException;

public interface AuthorisationService {
    User getUserByLoginAndPassword(String login, String password) throws UserNotFoundException, UserCantBeAuthorized;
}
