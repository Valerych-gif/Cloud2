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

public class ConnectionHandler {

    private final Logger logger = LogManager.getLogger(ConnectionHandler.class.getName());

    private final CloudConnection connection;

    public ConnectionHandler() {
        connection = new CloudConnection();
    }

    public CloudConnection init(ConnectWindowController controller) {
        controller.loginButton.setOnAction((event) -> {
            try {
                connectToServer(controller.urlTextField.getText(), controller.portTextField.getText());
                loginToServer(controller.loginTextField.getText(), controller.passwordTextField.getText());
                connection.setAuthorized(true);
                controller.close();
                if (controller.saveData.isSelected()) {
                    writeConnectionSettings(
                            controller.urlTextField.getText(),
                            controller.portTextField.getText(),
                            controller.loginTextField.getText(),
                            controller.passwordTextField.getText()
                    );
                } else {
                    clearConnectionSettings();
                }
            } catch (IOException e) {
                logger.error(String.format("Connection to server %s:%s failed. Cause: %s",
                        controller.urlTextField.getText(),
                        controller.portTextField.getText(),
                        e));
                controller.errorLabel.setText("There is trouble with connection. Please try again later.");
                disconnect();
            } catch (LoginUnsuccessfulException | BadResponseException e) {
                logger.error(String.format("Log in to server %s:%s for user %s failed. Cause: %s",
                        controller.urlTextField.getText(),
                        controller.portTextField.getText(),
                        controller.loginTextField.getText(),
                        e));
                controller.errorLabel.setText("Login or password isn't correct");
                disconnect();
            }
        });
        return connection;
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
    }

    public CloudConnection getConnection() {
        return connection;
    }

    private void writeConnectionSettings(String host, String port, String login, String password) {
        Settings.write("host", host);
        Settings.write("port", port);
        Settings.write("login", login);
        Settings.write("password", password);
    }

    private void clearConnectionSettings() {
        try {
            Settings.delete("host");
            Settings.delete("port");
            Settings.delete("login");
            Settings.delete("password");
        } catch (IOException e) {
            logger.error(String.format("Can't delete settings from settings file. Cause: %s", e));
        }
    }

    private void connectToServer(String host, String port) throws IOException {
        Socket socket = new Socket(host, Integer.parseInt(port));
        connection.setSocket(socket);
        connection.setInputStream(new DataInputStream(socket.getInputStream()));
        connection.setOutputStream(new DataOutputStream(socket.getOutputStream()));
    }

    private void loginToServer(String login, String password) throws LoginUnsuccessfulException, BadResponseException {
        connection.setLogin(login);
        connection.setPassword(password);
        LoginService loginService = new LoginService(connection);
        loginService.login();
    }

    private void registrationToServer(String login, String password) throws BadResponseException {
        connection.setLogin(login);
        connection.setPassword(password);
        LoginService loginService = new LoginService(connection);
        loginService.registration();
    }
}
