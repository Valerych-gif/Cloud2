package ru.valerych.cloud2.client.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.controllers.ConnectWindowController;
import ru.valerych.cloud2.client.controllers.WindowController;
import ru.valerych.cloud2.client.exceptions.LoginUnsuccessfulException;
import ru.valerych.cloud2.client.services.LoginService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler {

    private final Logger logger = LogManager.getLogger(ConnectionHandler.class.getName());

    private CloudConnection connection;
    private final ConnectWindowController controller;

    public ConnectionHandler(WindowController controller) {
        this.controller = (ConnectWindowController) controller;
    }

    public CloudConnection connect() {
        connection = new CloudConnection();
        controller.loginButton.setOnAction((event) -> {
            try {
                String login = controller.loginTextField.getText();
                String password = controller.passwordTextField.getText();
                String host = controller.urlTextField.getText();
                int port = Integer.parseInt(controller.portTextField.getText());
                connectToServer(host, port);
                loginToServer(login, password);
                controller.closeWindow();
            } catch (IOException e) {
                logger.error(String.format("Connection to server %s:%s failed. Cause: %s",
                        controller.urlTextField.getText(),
                        controller.portTextField.getText(),
                        e));
                controller.errorLabel.setText("There is trouble with connection. Please try again later.");
                disconnect();
            } catch (LoginUnsuccessfulException e) {
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

    private void connectToServer(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        connection.setSocket(socket);
        connection.setInputStream(new DataInputStream(socket.getInputStream()));
        connection.setOutputStream(new DataOutputStream(socket.getOutputStream()));
    }

    private void loginToServer(String login, String password) throws LoginUnsuccessfulException {
        connection.setLogin(login);
        connection.setPassword(password);
        LoginService loginService = new LoginService(connection);
        loginService.login();
    }

    public void disconnect() {
        try {
            connection.getOutputStream().close();
            connection.getInputStream().close();
            connection.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CloudConnection getConnection() {
        return connection;
    }
}
