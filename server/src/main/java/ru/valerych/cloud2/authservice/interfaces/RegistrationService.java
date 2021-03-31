package ru.valerych.cloud2.authservice.interfaces;

import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.LoginIsNotFreeException;

public interface RegistrationService {
    User getNewUserByLoginAndPassword(String login, String password) throws LoginIsNotFreeException;
}
