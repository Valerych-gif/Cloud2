package ru.valerych.cloud2.client.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.controllers.ConnectWindowController;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.exceptions.LoginUnsuccessfulException;
import ru.valerych.cloud2.client.services.LoginService;
import ru.valerych.cloud2.client.utils.Settings;
import ru.valerych.cloud2.commands.Requests;


import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler implements ConnectionSubject{

    private final Logger logger = LogManager.getLogger(ConnectionHandler.class.getName());

    private final CloudConnection connection;

    private final List<ConnectionObserver> observers;

    public ConnectionHandler() {
        observers = new ArrayList<>();
        connection = new CloudConnection();
    }

    public void connectToServer(String host, String port) throws IOException {
        Socket socket = new Socket(host, Integer.parseInt(port));
        connection.setSocket(socket);
        connection.setInputStream(new DataInputStream(socket.getInputStream()));
        connection.setOutputStream(new DataOutputStream(socket.getOutputStream()));
        notifyObservers();
    }

    public void loginToServer(String login, String password) throws LoginUnsuccessfulException, BadResponseException {
        connection.setLogin(login);
        connection.setPassword(password);
        LoginService loginService = new LoginService(connection);
        loginService.login();
        connection.setAuthorized(true);
        notifyObservers();
    }

    public void registrationToServer(String login, String password) throws BadResponseException {
        connection.setLogin(login);
        connection.setPassword(password);
        LoginService loginService = new LoginService(connection);
        loginService.registration();
        connection.setAuthorized(true);
        notifyObservers();
    }

    public void disconnect() {
        if (connection.getSocket() == null) return;
        List<Closeable> resources = new ArrayList<>();
        try {
            connection.getOutputStream().write(Requests.CLOSE_CONNECTION.get());
        } catch (IOException e) {
            logger.error(String.format("Can't send [%s] command. Cause: %s", Requests.CLOSE_CONNECTION, e));
        }
        resources.add(connection.getOutputStream());
        resources.add(connection.getInputStream());
        resources.add(connection.getSocket());
        resources.forEach(r -> {
            try {
                if (r != null) r.close();
            } catch (IOException e) {
                logger.error(String.format("Resource %s can't be closed. Cause: %s", r, e));
            }
        });
        notifyObservers();
    }

    public CloudConnection getConnection() {
        return connection;
    }

    @Override
    public void registerObserver(ConnectionObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ConnectionObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(observer -> observer.connectionUpdate(connection));
    }
}
