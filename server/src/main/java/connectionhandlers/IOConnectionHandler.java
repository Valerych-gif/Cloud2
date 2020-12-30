package connectionhandlers;

import fileserivices.iofileservices.IOFileServiceFactory;
import fileserivices.iofileservices.IOServerFileExplorer;
import fileserivices.iofileservices.IOServerFileExplorerFactory;
import network.ionetwork.IONetworkFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shareservice.iosharefileservices.IOShareFileServiceFactory;

import java.net.Socket;

public class IOConnectionHandler extends ConnectionHandler {

    private final Logger logger = LogManager.getLogger(IOConnectionHandler.class);

    public IOConnectionHandler(Socket socket){
        super();
        this.fileServiceFactory = new IOFileServiceFactory();
        this.networkFactory = new IONetworkFactory();
        this.shareFileServiceFactory = new IOShareFileServiceFactory();
        this.serverFileExplorerFactory = new IOServerFileExplorerFactory();
        this.network = networkFactory.createNetwork(socket);
        this.connectionHandler = this;
    }

    @Override
    public void run() {
        super.run();
    }
}
