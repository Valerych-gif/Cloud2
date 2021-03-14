package ru.valerych.cloud2.connectionhandlers;

import ru.valerych.cloud2.fileservices.iofileservices.IOFileServiceFactory;
import ru.valerych.cloud2.fileservices.iofileservices.IOServerFileExplorerFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.shareservice.iosharefileservices.IOShareFileServiceFactory;

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
