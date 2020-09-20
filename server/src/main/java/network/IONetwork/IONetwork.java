package network.IONetwork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.*;
import java.net.Socket;

import network.interfaces.Network;

public class IONetwork implements Network {

    private DataInputStream is;
    private DataOutputStream os;
    private Socket socket;

    private Logger logger = LogManager.getLogger(IONetwork.class);

    public IONetwork(Socket socket) {
        try {
            this.socket = socket;
            this.is = new DataInputStream(socket.getInputStream());
            this.os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
    }

    @Override
    public void sendDirContentToClient() {

    }

    @Override
    public void sendSharedFileNamesToClient() {

    }

    @Override
    public void sendBufferToClient(byte[] buffer, int numberOfBytes) {
        try {
            os.write(buffer, 0, numberOfBytes);
            os.flush();
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }

    }

    @Override
    public int readBufferFromClient(byte[] buffer) {
        int bytesRead = 0;
        try {
            bytesRead = is.read(buffer);
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
        return bytesRead;
    }

    @Override
    public byte readByteFromClient() {
        try {
            return is.readByte();
        } catch (IOException e) {
            LogUtils.error("Error of reading byte from client. " + e, logger);
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
            LogUtils.error("Error of reading bytes from client. " + e, logger);
        }
        return buffer;
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
        LogUtils.info("Client disconnected", logger);
    }

    @Override
    public void sendBytesToClient(byte[] buffer) {
        try {
            os.writeBytes(new String(buffer));
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
    }

    @Override
    public void sendByteToClient(byte response) {
        try {
            LogUtils.info(String.valueOf(response), logger, "->\t");
            os.writeByte(response);
        } catch (Exception e) {
            LogUtils.error("Can't to send response. " + e, logger);
        }
    }
}
