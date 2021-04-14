package ru.valerych.cloud2.client.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Network {

    private final Logger logger = LogManager.getLogger(Network.class.getName());

    private final DataInputStream is;
    private final DataOutputStream os;

    public Network(CloudConnection connection) {
        this.is = connection.getInputStream();
        this.os = connection.getOutputStream();
    }

    public void sendByteToServer(byte request) {
        try {
            os.writeByte(request);
        } catch (IOException e) {
            logger.error("Can't send request to server. Cause: " + e);
        }
    }

    public void sendBytesToServer(byte[] buffer) {
        try {
            os.write(buffer);
        } catch (IOException e) {
            logger.error("Can't send bytes to server. Cause: " + e);
        }
    }

    public byte readByteFromServer() {
        try {
            return is.readByte();
        } catch (IOException e) {
            logger.error("Error of reading byte from server. Cause " + e);
            return 0;
        }
    }

    public byte[] readBytesFromServer(int length) {
        byte[] buffer = new byte[length];
        try {
            for (int i = 0; i < length; i++) {
                buffer[i] = is.readByte();
            }
        } catch (IOException e) {
            logger.error("Error of reading bytes from server. " + e);
        }
        return buffer;
    }

    public long readLongFromServer(){
        try {
            return is.readLong();
        } catch (IOException e) {
            logger.error("Error of reading long from server. " + e);
            return 0L;
        }
    }

    public void sendLongToServer(long data){
        try {
            os.writeLong(data);
        } catch (IOException e){
            logger.error("Error of sending long to server. " + e);
        }
    }

//
//    @Override
//    public void closeConnection() {
//        try {
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.error(e);
//        }
//        try {
//            os.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.error(e);
//        }
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.error(e);
//        }
//        logger.info("Client disconnected");
//    }
}
