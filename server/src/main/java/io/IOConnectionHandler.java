package io;

import main.Cloud2Server;

import files.CloudFile;
import main.Commands;
import main.ConnectionHandler;

import java.io.*;
import java.net.Socket;

public class IOConnectionHandler extends ConnectionHandler{

    private static final int MAX_COMMAND_LENGTH = 100;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public IOConnectionHandler(Cloud2Server server, Socket socket) throws IOException{
        super(server);
        this.socket = socket;
        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run(){
        fileHandler = new IOFileHandler(this);
        while (isConnectionActive) {
            command = getCommandFromClient(); // Block
            super.run();
        }
    }

    public String getCommandFromClient(){

        StringBuilder command = new StringBuilder();
        try {
            for (int i = 0; i < MAX_COMMAND_LENGTH; i++) {
                byte b = is.readByte();
                command.append((char)b);
                if (checkCommand(command.toString())){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(command);
        return command.toString();
    }

    private boolean checkCommand(String command) {
        Commands[] commands = Commands.values();
        for (Commands c : commands) {
            if (command.equals(c.getCommandStr())) return true;
        }
        return false;
    }


    public void sendFileToClient() throws IOException {
        String fileName = is.readUTF();
        CloudFile file = new CloudFile(storage + "/" + fileName);
        fileHandler.getFileFromStorage(file);
    }

    public void receiveFileFromClient() throws IOException {
        String fileName = is.readUTF();
        long fileLength = is.readLong();
        CloudFile file = new CloudFile(storage + "/" + fileName, fileLength);
        fileHandler.loadFileToStorage(file);
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
