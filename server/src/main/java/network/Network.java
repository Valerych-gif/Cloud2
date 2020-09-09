package network;

public abstract class Network {
    public abstract void sendResponse(byte response);

    public abstract void sendDirContentToClient();

    public abstract void sendSharedFileNamesToClient();

    public abstract void sendBufferToClient(byte[] buffer, int byteRead);

    public abstract int readBufferFromClient(byte[] buffer);

    public abstract byte readByteFromClient();

    public abstract byte[] readBytesFromClient(int length);

    public abstract void closeConnection();

}
