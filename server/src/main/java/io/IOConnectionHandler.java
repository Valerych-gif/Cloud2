package io;

import main.*;

import files.CloudFile;

import java.io.*;
import java.net.Socket;

public class IOConnectionHandler extends ConnectionHandler {

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public IOConnectionHandler(Cloud2Server server, Socket socket) throws IOException {
        super(server);
        this.socket = socket;
        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        fileHandler = new IOFileHandler(this);
        super.run();
    }

    public Commands getCommandFromClient() {
        String command = getStringFromClient();
        return checkCommand(command);
    }

    private String getStringFromClient() {
        StringBuilder stringFromClient = new StringBuilder();
        char b = 0;
        try {
            while (true) {
                b = (char) is.readByte();
                if (b != '|') {
                    stringFromClient.append(b);
                } else {
                    System.out.println(stringFromClient.toString());
                    return stringFromClient.toString();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
        return stringFromClient.toString();
    }

    private Commands checkCommand(String command) {
        Commands[] commands = Commands.values();
        for (Commands c : commands) {
            if (command.equals(c.getString())) return c;
        }
        return null;
    }

    public void sendResponse(String responseStr) {
        try {
            System.out.println(responseStr);
            os.writeBytes(responseStr+Cloud2ServerStarter.END_COMMAND_CHAR);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public void sendFileToClient() {
        String fileName = getStringFromClient();
        CloudFile file = new CloudFile(storage + "/" + fileName);
        if (file.exists()) {
            sendResponse(Responses.OK.getString());
            fileHandler.getFileFromStorage(file);
        } else {
            // todo Обработчик ошибки
            System.out.println("Неправильное имя файла");
        }
    }

    public void receiveFileFromClient() {

        boolean isOk = true;
        String fileName = getFileNameFromClient();
        if (fileName.length() > 0) {
            sendResponse(Responses.OK.getString());
        } else {
            // todo Обработчик ошибки
            System.out.println("Неправильное имя файла");
            isOk = false;
        }

        long fileLength = getFileLengthFromClient();
        if (fileLength > 0) {
            sendResponse(Responses.OK.getString());
        } else {
            // todo Обработчик ошибки
            System.out.println("Неправильный размер");
            isOk = false;
        }

        if (isOk) {
            CloudFile file = new CloudFile(storage + "/" + fileName, fileLength);
            fileHandler.loadFileToStorage(file);
        }
    }

    private long getFileLengthFromClient() {
        String fileLengthStr = getStringFromClient();
        return Long.parseLong(fileLengthStr);
    }

    private String getFileNameFromClient() {
        return getStringFromClient();
    }

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

    public File getStorage() {
        return storage;
    }
}
