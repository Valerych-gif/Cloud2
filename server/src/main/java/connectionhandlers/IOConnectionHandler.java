package connectionhandlers;

import authservice.UsersService;

import network.IOCommandReceiver;
import network.IONetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class IOConnectionHandler extends ConnectionHandler {

    private Logger logger = LogManager.getLogger(IOConnectionHandler.class);

    public IOConnectionHandler(Socket socket){
        super();
        this.network = new IONetwork(socket);
        this.commandReceiver = new IOCommandReceiver(network);
        this.usersService = new UsersService(network);
        this.connectionHandler = this;
    }

    @Override
    public void run() {
        super.run();
    }
}
