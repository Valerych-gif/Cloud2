package ru.valerych.cloud2.authservice.interfaces;

import ru.valerych.cloud2.entities.User;

public interface AuthorisationService {
    User getUserByLoginAndPassword(String login, String password);
}
