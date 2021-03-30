package ru.valerych.cloud2.authservice.interfaces;

import ru.valerych.cloud2.entities.User;

public interface UsersService {
    User authUserByLoginAndPassword();
    User registrationUserByLoginAndPassword();
}
