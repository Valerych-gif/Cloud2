package connectionhandlers;

import authservice.UsersService;

import fileserivices.IOFileservices.IOFileServiceFactory;
import network.IONetwork.IOCommandReceiver;
import network.IONetwork.IONetwork;
import network.IONetwork.IONetworkFactory;
import network.interfaces.NetworkFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class IOConnectionHandler extends ConnectionHandler {

    private Logger logger = LogManager.getLogger(IOConnectionHandler.class);

    public IOConnectionHandler(Socket socket){
        super();
        this.fileServiceFactory = new IOFileServiceFactory();
        this.networkFactory = new IONetworkFactory();
        this.network = networkFactory.createNetwork(socket);
        this.connectionHandler = this;
    }

    @Override
    public void run() {
        super.run();
    }
}
