package network.interfaces;

public interface Network {
    void sendByteToClient(byte response);

    void sendBufferToClient(byte[] buffer);

    int readBufferFromClient(byte[] buffer);

    byte readByteFromClient();

    byte[] readBytesFromClient(int length);

    void closeConnection();

    void sendBytesToClient(byte[] buffer);

    long getLongFromClient();
}
