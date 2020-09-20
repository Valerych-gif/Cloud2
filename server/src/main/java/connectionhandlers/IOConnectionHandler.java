package connectionhandlers;

import fileserivices.iofileservices.IOFileServiceFactory;
import network.IONetwork.IONetworkFactory;
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
