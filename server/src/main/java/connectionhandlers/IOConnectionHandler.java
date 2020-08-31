package connectionhandlers;

import commands.Commands;
import commands.Responses;
import filehandlers.IOFileHandler;

import entities.CloudFile;
import network.IOCommandReceiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

import java.io.*;
import java.net.Socket;

public class IOConnectionHandler extends ConnectionHandler {

    private Logger logger = LogManager.getLogger(IOConnectionHandler.class);

    private Socket socket;
    private IOCommandReceiver commandReceiver;
    private DataInputStream is;
    private DataOutputStream os;

    public IOConnectionHandler(Socket socket) throws IOException {
        super();
        this.socket = socket;
        this.commandReceiver = new IOCommandReceiver(socket);
        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());
        this.connectionHandler = this;
    }

    @Override
    public void run() {
        super.run();
    }



    public Commands getCommandFromClient() throws IOException {
        byte signalByte = commandReceiver.getSignalByteFromClient();
        return checkCommand(signalByte);
    }

    public String getStringFromClient() throws IOException {
        StringBuilder stringFromClient = new StringBuilder();
        char b = 0;

        while (true) {
            b = (char) is.readByte();
            if (b != Cloud2ServerSettings.END_COMMAND_CHAR) {
                stringFromClient.append(b);
            } else {
                System.out.println("<-\t" + stringFromClient.toString());
                return stringFromClient.toString();
            }
        }
    }

    private Commands checkCommand(byte signalByte) {
        Commands[] commands = Commands.values();
        for (Commands c : commands) {
            if (signalByte==c.get()) return c;
        }
        return null;
    }

    public void sendResponse(String responseStr) {
        try {
            System.out.println("->\t" + responseStr);
            os.writeBytes(responseStr + Cloud2ServerSettings.END_COMMAND_CHAR);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public void sendFileToClient() throws IOException {
        String fileName = getStringFromClient();
        fileHandler.getFileFromStorage(fileName);
    }

    public void receiveFileFromClient() throws IOException {

        boolean isOk = true;
        String fileName = getFileNameFromClient();
        if (fileName.length() > 0) {
            sendResponse(Responses.OK.getString());
        } else {
            System.out.println("Неправильное имя файла");
            isOk = false;
        }

        long fileLength = getFileLengthFromClient();
        if (fileLength >= 0) {
            sendResponse(Responses.OK.getString());
        } else {
            System.out.println("Неправильный размер");
            isOk = false;
        }

        if (isOk) {
            CloudFile file = new CloudFile(mainStorage + "/" + fileName, fileLength);
            fileHandler.loadFileToStorage(file);
        }
    }

    private long getFileLengthFromClient() throws IOException {
        String fileLengthStr = getStringFromClient();
        return Long.parseLong(fileLengthStr);
    }

    private String getFileNameFromClient() throws IOException {
        return getStringFromClient();
    }

    public void deleteFileFromStorage() throws IOException {
        String fileName = getStringFromClient();
        fileHandler.deleteFileFromStorage(fileName);
    }

    public void sendDirContent() throws IOException {
        fileHandler.sendDirContentToClient();
    }

    public void sendSharedFilesToClient() {
        fileHandler.sendSharedFileNamesToClient();
    }

//    public void shareFile() throws IOException {
//        String nickName = getStringFromClient();
//        String fileName = getStringFromClient();
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
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
        logger.info("Client disconnected");
    }

    public DataInputStream getDataInputStream() {
        return is;
    }

    public DataOutputStream getDataOutputStream() {
        return os;
    }

    public Socket getSocket() {
        return socket;
    }

}
