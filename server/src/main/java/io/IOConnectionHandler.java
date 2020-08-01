package io;

import main.*;

import files.CloudFile;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
        String stringFromClient = null;
        char[] b = new char[Cloud2ServerStarter.BUFFER_SIZE];
        try {
            Reader in = new InputStreamReader(is, StandardCharsets.UTF_8);
            int readInt = in.read(b, 0, b.length);
            stringFromClient = String.copyValueOf(b, 0, readInt);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
        return stringFromClient;
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
            Thread.sleep(100); // todo Костыль, надо избавиться. Без задержки строки от клиента приходят не полностью
            os.writeBytes(responseStr);
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public void sendFileToClient() {
        String fileName = getStringFromClient();
        CloudFile file = new CloudFile(storage + "/" + fileName);
        if (fileName != null && file.exists()) {
            sendResponse(Responses.OK.getString());
            fileHandler.getFileFromStorage(file);
        }else {
            // todo Обработчик ошибки
            System.out.println("Неправильное имя файла");
        }
    }

    public void receiveFileFromClient() {

        boolean isOk = true;
        String fileName = getFileNameFromClient();
        if (fileName != null && fileName.length() > 0) {
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
