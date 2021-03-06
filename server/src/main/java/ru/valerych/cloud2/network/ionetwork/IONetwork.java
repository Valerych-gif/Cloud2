package ru.valerych.cloud2.network.ionetwork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

import ru.valerych.cloud2.network.interfaces.Network;

public class IONetwork implements Network {

    private DataInputStream is;
    private DataOutputStream os;
    private Socket socket;

    private final Logger logger = LogManager.getLogger(IONetwork.class);

    public IONetwork(Socket socket) {
        try {
            this.socket = socket;
            this.is = new DataInputStream(socket.getInputStream());
            this.os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void sendBufferToClient(byte[] buffer) {
        try {
            os.write(buffer, 0, buffer.length);
            os.flush();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    @Override
    public int readBufferFromClient(byte[] buffer) {
        int bytesRead = 0;
        try {
            bytesRead = is.read(buffer);
        } catch (IOException e) {
            logger.error(e.toString());
        }
        return bytesRead;
    }

    @Override
    public byte readByteFromClient() {
        try {
            return is.readByte();
        } catch (IOException e) {
            logger.error("Error of reading byte from client. " + e);
            return 0;
        }
    }

    @Override
    public byte[] readBytesFromClient(int length) {
        byte[] buffer = new byte[length];
        try {
            for (int i = 0; i < length; i++) {
                buffer[i] = is.readByte();
            }
        } catch (IOException e) {
            logger.error("Error of reading bytes from client. " + e);
        }
        return buffer;
    }

    @Override
    public void sendBytesToClient(byte[] buffer) {
        try {
            for (byte b : buffer) {
                os.writeByte(b);
            }
        } catch (IOException e) {
            logger.error(e.toString(), logger);
        }
    }

    @Override
    public void sendByteToClient(byte response) {
        try {
            os.writeByte(response);
        } catch (Exception e) {
            logger.error("Can't to send response. " + e);
        }
    }


    public long getLongFromClient(){
        try {
            return is.readLong();
        } catch (IOException e) {
            logger.error("Error of getting long from client. " + e);
            return 0L;
        }
    }

    @Override
    public void sendLongToClient(long value){
        try {
            os.writeLong(value);
        } catch (IOException e) {
            logger.error("Error of sending long to client. " + e);
        }
    }

    @Override
    public void closeConnection() {
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
}
