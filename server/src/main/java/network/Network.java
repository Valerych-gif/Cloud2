package network;

import java.net.Socket;

public abstract class Network {
    public abstract void sendResponse(String string);

    public abstract void sendDirContentToClient();

    public abstract void sendSharedFileNamesToClient();

    public abstract void sendBufferToClient(byte[] buffer, int byteRead);

    public abstract int readBufferFromClient(byte[] buffer);

    public abstract byte readByteFromClient();

    public abstract byte[] readBytesFromClient(int length);

    public abstract void closeConnection();

    public abstract Socket getSocket();
}
