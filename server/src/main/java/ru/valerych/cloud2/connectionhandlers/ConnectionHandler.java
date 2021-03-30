package ru.valerych.cloud2.connectionhandlers;

import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.authservice.IOUsersService;
import ru.valerych.cloud2.fileservices.interfaces.FileService;
import ru.valerych.cloud2.fileservices.interfaces.FileServiceFactory;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorerFactory;
import ru.valerych.cloud2.network.ionetwork.IOCommandReceiver;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.servers.Cloud2Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.shareservice.interfaces.ShareFileService;
import ru.valerych.cloud2.shareservice.interfaces.ShareFileServiceFactory;

import java.io.File;

public abstract class ConnectionHandler implements Runnable {

    private final Logger logger = LogManager.getLogger(ConnectionHandler.class);

    protected Cloud2Server server;
    protected ConnectionHandler connectionHandler;
    protected FileServiceFactory fileServiceFactory;
    protected ShareFileServiceFactory shareFileServiceFactory;
    protected NetworkFactory networkFactory;
    protected ServerFileExplorerFactory serverFileExplorerFactory;
    protected ServerFileExplorer serverFileExplorer;
    protected FileService fileService;
    protected IOUsersService usersService;
    protected ShareFileService shareFileService;
    protected Network network;
    protected IOCommandReceiver commandReceiver;
    protected File mainStorage;
    protected boolean isConnectionActive;
    protected User user;

    public ConnectionHandler() {
        Cloud2Server server = Cloud2Server.getInstance();
        logger.info("Connection accepted");
        this.isConnectionActive = true;
        this.server = server;
        this.mainStorage = server.getStorage();
        this.user = User.UNAUTHORIZED_USER;
    }

    @Override
    public void run() {
        this.commandReceiver = new IOCommandReceiver(network);
        this.usersService = new IOUsersService(network);

        while (isConnectionActive) {
            Requests command = null;
            try {
                while (command == null) {
                    logger.info("Waiting for signal byte from client");
                    command = commandReceiver.getCommandFromClient(); // Block
                }
                switch (command) {
                    case CONNECTION_LOST:
                        closeConnection();
                        break;
                    case AUTHORIZATION:
                        network.sendByteToClient(Responses.OK.getSignalByte());
                        this.user = usersService.authUserByLoginAndPassword();
                        if (user != User.UNAUTHORIZED_USER) {
                            network.sendByteToClient(Responses.OK.getSignalByte());
                            setUpServices();
                        } else {
                            network.sendByteToClient(Responses.FAIL.getSignalByte());
                        }
                        break;
                    case REGISTRATION:
                        network.sendByteToClient(Responses.OK.getSignalByte());
                        this.user = usersService.registrationUserByLoginAndPassword();
                        if (user != User.UNAUTHORIZED_USER) {
                            network.sendByteToClient(Responses.OK.getSignalByte());
                            setUpServices();
                        } else {
                            network.sendByteToClient(Responses.FAIL.getSignalByte());
                        }
                        break;
                    case UPLOAD:
                        if (user != User.UNAUTHORIZED_USER) {
                            network.sendByteToClient(Responses.OK.getSignalByte());
                            fileService.receiveFileFromClient();
                            network.sendByteToClient(Responses.OK.getSignalByte());
                        } else {
                            network.sendByteToClient(Responses.FAIL.getSignalByte());
                        }
                        break;
                    case DOWNLOAD:
                        if (user != User.UNAUTHORIZED_USER) {
                            network.sendByteToClient(Responses.OK.getSignalByte());
                            fileService.sendFileToClient();  // Block
                            network.sendByteToClient(Responses.OK.getSignalByte());
                        } else {
                            network.sendByteToClient(Responses.FAIL.getSignalByte());
                        }
                        break;
                    case DELETE:
                        if (user != User.UNAUTHORIZED_USER) {
                            network.sendByteToClient(Responses.OK.getSignalByte());
                            fileService.deleteFileFromServer();
                        } else {
                            network.sendByteToClient(Responses.FAIL.getSignalByte());
                        }
                        break;
                    case GET_DIR_CONTENT:
                        if (user != User.UNAUTHORIZED_USER) {
                            network.sendByteToClient(Responses.OK.getSignalByte());
                            fileService.sendDirContent();
                        } else {
                            network.sendByteToClient(Responses.FAIL.getSignalByte());
                        }
                        break;
                    case GET_SHARED_DIR_CONTENT:
                        if (user != User.UNAUTHORIZED_USER) {
                            network.sendByteToClient(Responses.OK.getSignalByte());
                            shareFileService.sendSharedFilesDirToClient();
                        } else {
                            network.sendByteToClient(Responses.FAIL.getSignalByte());
                        }
                        break;
                    case SHARE:
                        if (user != User.UNAUTHORIZED_USER) {
                            network.sendByteToClient(Responses.OK.getSignalByte());
                            shareFileService.shareFileByCommandFromClient();
                        } else {
                            network.sendByteToClient(Responses.FAIL.getSignalByte());
                        }
                        break;
                    case CLOSE_CONNECTION:
                        closeConnection();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                closeConnection();
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    private void setUpServices() {
        this.serverFileExplorer = serverFileExplorerFactory.createServerFileExplorer(user, network);
        this.fileService = fileServiceFactory.createFileService(network, serverFileExplorer);
        this.shareFileService = shareFileServiceFactory.createShareFileService(user, network, serverFileExplorer);
    }

    public void closeConnection() {
        isConnectionActive = false;
        network.closeConnection();
    }
}
