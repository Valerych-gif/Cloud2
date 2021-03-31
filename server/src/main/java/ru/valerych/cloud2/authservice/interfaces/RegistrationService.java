package ru.valerych.cloud2.authservice.interfaces;

import ru.valerych.cloud2.entities.User;

public interface RegistrationService {
    User getNewUserByLoginAndPassword(String login, String password);
}
