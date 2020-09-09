package connectionhandlers;

import commands.Commands;
import commands.Responses;
import entities.User;
import authservice.UsersService;
import filehandlers.IOFileHandler;
import network.IOCommandReceiver;
import network.Network;
import servers.Cloud2Server;
import filehandlers.FileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.File;
import java.io.IOException;

public abstract class ConnectionHandler implements Runnable {

    private Logger logger = LogManager.getLogger(ConnectionHandler.class);

    protected Cloud2Server server;
    protected ConnectionHandler connectionHandler;
    protected FileHandler fileHandler;
    protected UsersService usersService;
    protected Network network;
    protected IOCommandReceiver commandReceiver;
    protected File mainStorage;
    protected boolean isConnectionActive;
    protected User user;

    public ConnectionHandler() {
        Cloud2Server server = Cloud2Server.getInstance();
        LogUtils.info("Connection accepted", logger);
        this.isConnectionActive = true;
        this.server = server;
        this.mainStorage = server.getStorage();
        this.user = new User(-1, "", "");
    }

    @Override
    public void run() {
        while (isConnectionActive) {
            Commands command = null;
            try {
                while (command == null) {
                    LogUtils.info("Waiting for signal byte from client", logger);
                    command = commandReceiver.getCommandFromClient(); // Block
                }
                switch (command) {
                    case AUTHORIZATION:
                        network.sendResponse(Responses.OK.getString());
                        this.user = usersService.getUserByLoginAndPass();
                        if (user.getId() != -1) {
                            network.sendResponse(Responses.OK.getString());
                            this.fileHandler = new IOFileHandler(user, network);
                        } else {
                            network.sendResponse(Responses.FAIL.getString());
                        }
                        break;
//                    case REGISTRATION:
//                        network.sendResponse(Responses.OK.getString());
//                        this.user = usersService.getNewUserByLoginAndPass();
//                        if (user != null) {
//                            network.sendResponse(Responses.OK.getString());
//                            user.setUpNewUser();
//                        } else {
//                            network.sendResponse(Responses.FAIL.getString());
//                        }
//                        break;
//                    case UPLOAD:
//                        if (user!=null) {
//                            network.sendResponse(Responses.OK.getString());
//                            receiveFileFromClient(); // Block
//                        } else {
//                            network.sendResponse(Responses.FAIL.getString());
//                        }
//                        break;
//                    case DOWNLOAD:
//                        if (user!=null) {
//                            network.sendResponse(Responses.OK.getString());
//                            sendFileToClient();
//                        } else {
//                            network.sendResponse(Responses.FAIL.getString());
//                        }
//                        break;
//                    case DELETE:
//                        if (user!=null) {
//                            network.sendResponse(Responses.OK.getString());
//                            deleteFileFromStorage();
//                        } else {
//                            network.sendResponse(Responses.FAIL.getString());
//                        }
//                        break;
//                    case GET_DIR_CONTENT:
//                        if (user!=null) {
//                            network.sendResponse(Responses.OK.getString());
//                            sendDirContent();
//                        } else {
//                            network.sendResponse(Responses.FAIL.getString());
//                        }
//                        break;
//                    case GET_SHARED_DIR_CONTENT:
//                        if (user!=null) {
//                            network.sendResponse(Responses.OK.getString());
//                            sendSharedFilesToClient();
//                        } else {
//                            network.sendResponse(Responses.FAIL.getString());
//                        }
//                        break;
//                    case SHARE:
//                        if (user!=null) {
//                            network.sendResponse(Responses.OK.getString());
//                            authService.shareFile();
//                        } else {
//                            network.sendResponse(Responses.FAIL.getString());
//                        }
//                        break;
//                    case CLOSE_CONNECTION:
//                        closeConnection();
//                        break;
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

//    public void sendFileToClient(){
//        String fileName = network.getStringFromClient();
//        fileHandler.getFileFromStorage(fileName);
//    }


//    public void sendSharedFileNamesToClient() {
//        try {
//            File[] files;
//            files = authService.getSharedFiles(connectionHandler.getUserId());
//            for (File f : Objects.requireNonNull(files)) {
//                String fileName = f.getAbsolutePath();
//                File storageAbsPath = new File(Cloud2ServerSettings.STORAGE_ROOT_DIR);
//                int storageAbsPathLength = storageAbsPath.getAbsolutePath().length();
//                String fileNameToSend = fileName.substring(storageAbsPathLength);
//                System.out.println(fileNameToSend);
//                sendFileNameToClient(fileNameToSend);
//            }
//            sendResponse(Responses.END_OF_DIR_CONTENT.getString());
//        } catch (IOException e) {
//            logger.error(e);
//            e.printStackTrace();
//        }
//
//    }

    public void sendDirContent() {
//        String requestedDirFromClient = network.getStringFromClient();
//        fileHandler.setCurrentStorageDir(requestedDirFromClient);
//
//        if (currentStorageDir.getAbsolutePath().length() > rootStorageDir.getAbsolutePath().length()) {
//            sendResponse(DIR_PREFIX + PARENT_DIR_MARK);
//        }
//        for (File f : Objects.requireNonNull(currentStorageDir.listFiles())) {
//            String fileName = f.getName();
//            sendFileNameToClient(fileName);
//        }
//        sendResponse(Responses.END_OF_DIR_CONTENT.getString());
    }

    private void sendFileNameToClient(String fileName) {
        File f = new File(fileName);
        if (f.isDirectory()) {
            network.sendResponse(IOFileHandler.DIR_PREFIX + fileName);
        } else {
            network.sendResponse(IOFileHandler.FILE_PREFIX + fileName);
        }
    }
//
//    public void receiveFileFromClient(){
//        boolean isOk = true;
//        String fileName = getFileNameFromClient();
//        if (fileName.length() > 0) {
//            network.sendResponse(Responses.OK.getString());
//        } else {
//            System.out.println("Неправильное имя файла");
//            isOk = false;
//        }
//
//        long fileLength = getFileLengthFromClient();
//        if (fileLength >= 0) {
//            network.sendResponse(Responses.OK.getString());
//        } else {
//            System.out.println("Неправильный размер");
//            isOk = false;
//        }
//
//        if (isOk) {
//            CloudFile file = new CloudFile(mainStorage + "/" + fileName, fileLength);
//            fileHandler.loadFileToStorage(file);
//        }
//    }

//    private long getFileLengthFromClient(){
//        String fileLengthStr = network.getStringFromClient();
//        return Long.parseLong(fileLengthStr);
//    }
//
//    private String getFileNameFromClient(){
//        return network.getStringFromClient();
//    }
//
//    public void deleteFileFromStorage(){
//        String fileName = network.getStringFromClient();
//        fileHandler.deleteFileFromStorage(fileName);
//    }

    public void sendSharedFilesToClient() {
        network.sendSharedFileNamesToClient();
    }

    //    public void shareFile(){
//        String nickName = network.getStringFromClient();
//        String fileName = network.getStringFromClient();
//        String fileFullPathName = fileHandler.getAbsFilePathByName(fileName).getAbsolutePath();
//        String fileNameForShare = fileFullPathName.substring(fileHandler.getStorageRootDirPath().length());
//        String userIdStr = String.valueOf(userId);
//        System.out.println(fileNameForShare);
//        try {
//            authService.shareFile(nickName, userIdStr, fileNameForShare);
//        } catch (IOException e) {
//            logger.error(e);
//            e.printStackTrace();
//        }
//    }
    public void closeConnection() {
        isConnectionActive = false;
        network.closeConnection();
    }

    public User getUser() {
        return user;
    }
}
