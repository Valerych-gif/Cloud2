package network.interfaces;

public interface Network {
    void sendByteToClient(byte response);

    void sendDirContentToClient();

    void sendSharedFileNamesToClient();

    void sendBufferToClient(byte[] buffer, int byteRead);

    int readBufferFromClient(byte[] buffer);

    byte readByteFromClient();

    byte[] readBytesFromClient(int length);

    void closeConnection();

    void sendBytesToClient(byte[] buffer);
}
