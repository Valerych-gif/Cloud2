package ru.valerych.cloud2.client.services;

import ru.valerych.cloud2.client.exceptions.LoginUnsuccessfulException;
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.network.Network;

import java.nio.charset.StandardCharsets;

import static ru.valerych.cloud2.commands.Requests.*;
import static ru.valerych.cloud2.commands.Responses.*;

public class LoginService {
    private final CloudConnection connection;
    private final String login;
    private final String password;

    public LoginService(CloudConnection connection) {
        this.connection = connection;
        this.login = connection.getLogin();
        this.password = connection.getPassword();
    }

    public void login() throws LoginUnsuccessfulException {
        Network network = new Network(connection);
        network.sendByteToServer(AUTHORIZATION.get());
        if (network.readByteFromServer()==FAIL.getSignalByte()) throw new LoginUnsuccessfulException("Server didn't response [OK]. Something went wrong");
        byte loginLength = (byte) login.length();
        network.sendByteToServer(loginLength);
        if (network.readByteFromServer()==FAIL.getSignalByte()) throw new LoginUnsuccessfulException("Server didn't response [OK]. Login length wasn't send");
        network.sendBytesToServer(login.getBytes(StandardCharsets.UTF_8));
        if (network.readByteFromServer()==FAIL.getSignalByte()) throw new LoginUnsuccessfulException("Server didn't response [OK]. Login wasn't send");
        byte passwordLength = (byte) password.length();
        network.sendByteToServer(passwordLength);
        if (network.readByteFromServer()==FAIL.getSignalByte()) throw new LoginUnsuccessfulException("Server didn't response [OK]. Password length wasn't send");
        network.sendBytesToServer(password.getBytes(StandardCharsets.UTF_8));
        if (network.readByteFromServer()==FAIL.getSignalByte()) throw new LoginUnsuccessfulException("Server didn't response [OK]. Password wasn't send");
        if (network.readByteFromServer()==FAIL.getSignalByte()) throw new LoginUnsuccessfulException("Server didn't response [OK]. Login or password isn't correct");
    }
}
