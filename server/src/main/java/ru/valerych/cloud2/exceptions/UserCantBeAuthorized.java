package ru.valerych.cloud2.exceptions;

public class UserCantBeAuthorized extends Exception {
    public UserCantBeAuthorized(String message) {
        super(message);
    }
}
