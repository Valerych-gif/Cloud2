package connectionhandlers;

import authservice.UsersService;

import network.IOCommandReceiver;
import network.IONetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class IOConnectionHandler extends ConnectionHandler {

    private Logger logger = LogManager.getLogger(IOConnectionHandler.class);

    public IOConnectionHandler(Socket socket) throws IOException {
        super();
        this.network = new IONetwork(socket);
        this.commandReceiver = new IOCommandReceiver(network);
        this.usersService = UsersService.getInstance(network);
        this.connectionHandler = this;
    }

    @Override
    public void run() {
        super.run();
    }

}
