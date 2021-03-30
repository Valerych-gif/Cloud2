package ru.valerych.cloud2.network.ionetwork;

import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.utils.Client;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import org.junit.jupiter.api.*;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class IONetworkTest {
    private static Network network;
    private static IOCommandReceiver ioCommandReceiver;
    private static Client client;
    private static int bufferSize;
    private static ServerSocket serverSocket;

    @BeforeAll
    static void networkInit(){
        bufferSize = Cloud2ServerSettings.BUFFER_SIZE;
        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            new Thread(()->{
                client = new Client();
            }).start();
            Thread.sleep(500);
            Socket socket = serverSocket.accept();
            NetworkFactory networkFactory = new IONetworkFactory();
            network = networkFactory.createNetwork(socket);
            ioCommandReceiver = new IOCommandReceiver(network);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clearResources(){
        try {
            serverSocket.close();
            network.closeConnection();
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Sending buffer to client is success")
    public void sendBufferToClientSuccessTest(){
        byte[] sentBuffer = getRandomBufferContent();
        network.sendBufferToClient(sentBuffer);
        byte[] receivedBuffer = client.getBytesFromServer(sentBuffer.length);
        Assertions.assertArrayEquals(sentBuffer, receivedBuffer);
    }

    @Test
    @DisplayName("Reading buffer client is success")
    public void readBufferFromClientSuccessTest(){
        byte[] sentBuffer = getRandomBufferContent();
        client.sendBytesToServer(sentBuffer);
        byte[] receivedBuffer = new byte[bufferSize];
        network.readBufferFromClient(receivedBuffer);
        Assertions.assertArrayEquals(sentBuffer, receivedBuffer);
    }

    @Test
    @DisplayName("Reading one byte from client is success")
    public void readByteFromClientSuccessTest(){
        byte[] sentByte = new byte[] {getRandomByte()};
        client.sendBytesToServer(sentByte);
        byte receiveByte = network.readByteFromClient();
        Assertions.assertEquals(sentByte[0], receiveByte);
    }

    @Test
    @DisplayName("Reading bytes client is success")
    public void readBytesFromClientSuccessTest(){
        String string = "Hello World";
        byte[] stringBytes = string.getBytes();
        client.sendBytesToServer(stringBytes);
        byte[] bytes = network.readBytesFromClient(stringBytes.length);
        Assertions.assertArrayEquals(stringBytes, bytes);
    }

    @Test
    @DisplayName("Sending bytes to client is success")
    public void sendBytesToClientSuccessTest(){
        byte[] sentBytes = getRandomBufferContent();
        network.sendBytesToClient(sentBytes);
        byte[] receivedBytes = client.getBytesFromServer(sentBytes.length);
        Assertions.assertArrayEquals(sentBytes, receivedBytes);
    }

    @Test
    @DisplayName("Sending one byte to client is success")
    public void sendByteToClientSuccessTest(){
        byte sentByte = new byte[] {getRandomByte()}[0];
        network.sendByteToClient(sentByte);
        byte receivedByte = client.getBytesFromServer(1)[0];
        Assertions.assertEquals(sentByte, receivedByte);
    }

    @Test
    @DisplayName("Getting Long from client is success")
    public void getLongFromClientSuccessTest(){
        Random random = new Random();
        long sentLong = random.nextLong();
        client.sendLongToServer(sentLong);
        long receivedLong = network.getLongFromClient();
        Assertions.assertEquals(sentLong, receivedLong);
    }

    @Test
    @DisplayName("Getting signal byte from client is success")
    public void getSignalByteFromClientSuccessTest(){
        byte[] sentByte1 = new byte[]{50};
        client.sendBytesToServer(sentByte1);
        byte receivedByte1 = ioCommandReceiver.getSignalByteFromClient();
        byte[] sentByte2 = new byte[]{90};
        client.sendBytesToServer(sentByte2);
        byte receivedByte2 = ioCommandReceiver.getSignalByteFromClient();
        byte[] sentByte3 = new byte[]{30};
        client.sendBytesToServer(sentByte3);
        byte receivedByte3 = ioCommandReceiver.getSignalByteFromClient();
        Assertions.assertEquals(sentByte1[0], receivedByte1);
        Assertions.assertEquals(sentByte2[0], receivedByte2);
        Assertions.assertEquals(sentByte3[0], receivedByte3);
    }

    @Test
    @DisplayName("Getting command from client is success")
    public void getCommandFromClientSuccessTest(){
        byte[] sentByte1 = new byte[]{50};
        client.sendBytesToServer(sentByte1);
        Requests receivedCommand1 = ioCommandReceiver.getCommandFromClient();
        byte[] sentByte2 = new byte[]{90};
        client.sendBytesToServer(sentByte2);
        Requests receivedCommand2 = ioCommandReceiver.getCommandFromClient();
        byte[] sentByte3 = new byte[]{100};
        client.sendBytesToServer(sentByte3);
        Requests receivedCommand3 = ioCommandReceiver.getCommandFromClient();
        Assertions.assertEquals(receivedCommand1, Requests.GET_DIR_CONTENT);
        Assertions.assertEquals(receivedCommand2, Requests.SHARE);
        Assertions.assertNull(receivedCommand3);
    }

    private byte[] getRandomBufferContent(){
        int bufferSize = Cloud2ServerSettings.BUFFER_SIZE;
        byte[] sentBuffer = new  byte[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            sentBuffer[i] = getRandomByte();
        }
        return sentBuffer;
    }

    private byte getRandomByte(){
        Random random = new Random();
        return (byte)random.nextInt(127);
    }
}
