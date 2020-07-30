package io;

import files.IOFileHandler;
import main.Cloud2Server;

import files.CloudFile;
import files.FileHandler;
import lombok.Getter;
import main.ConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class IOConnectionHandler extends ConnectionHandler{

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
        while (isConnectionActive) {
            fileHandler = new IOFileHandler(this);
            command = getCommandFromClient(); // Block
            super.run();
        }
    }

    public String getCommandFromClient(){
        String command = null;
        try {
            command = is.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return command;
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
