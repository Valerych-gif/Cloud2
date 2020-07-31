package io;

import main.Cloud2Server;

import files.CloudFile;
import main.Cloud2ServerStarter;
import main.Commands;
import main.ConnectionHandler;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class IOConnectionHandler extends ConnectionHandler {

    private static final int MAX_COMMAND_LENGTH = 100;
    private static final int MAX_FILE_NAME_LENGTH = 100;
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
        while (isConnectionActive) {
            command = getCommandFromClient(); // Block
            super.run();
        }
    }

    public Commands getCommandFromClient() {

        StringBuilder command = new StringBuilder();
        try {
            for (int i = 0; i < MAX_COMMAND_LENGTH; i++) {
                byte b = is.readByte();
                command.append((char) b);
                Commands checkedCommand = checkCommand(command.toString());
                if (checkedCommand != null) {
                    return checkedCommand;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Commands checkCommand(String command) {
        Commands[] commands = Commands.values();
        for (Commands c : commands) {
            if (command.equals(c.getCommandStr())) return c;
        }
        return null;
    }

    public void sendResponse(String responseStr) {
        try {
            os.writeBytes(responseStr);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void sendFileToClient() throws IOException {
        String fileName = is.readUTF();
        CloudFile file = new CloudFile(storage + "/" + fileName);
        fileHandler.getFileFromStorage(file);
    }

    public void receiveFileFromClient() throws IOException {
        String fileName = getFileNameFromClient();//is.readUTF();
        long fileLength = is.readLong();
        CloudFile file = new CloudFile(storage + "/" + fileName, fileLength);
        fileHandler.loadFileToStorage(file);
    }

    private String getFileNameFromClient() {
//        String fileName=null;
//        try {
//            byte[] b = new byte[Cloud2ServerStarter.BUFFER_SIZE];
//            int intRead = is.read(b);
//            fileName = new String(b);
//            //System.out.println(Arrays.toString(b));
//
////                if (b<0){
////                    System.out.println(fileName.toString());
////                    return fileName.toString();
////                }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(fileName);
//        return fileName;
        return null;
    }

    public void closeConnection() {

        isConnectionActive = false;
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
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
