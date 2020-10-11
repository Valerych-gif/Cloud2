package main.io;

import commands.Requests;
import mocks.Client;
import network.interfaces.Network;
import network.interfaces.NetworkFactory;
import network.ionetwork.IOCommandReceiver;
import network.ionetwork.IONetworkFactory;
import org.junit.jupiter.api.*;
import settings.Cloud2ServerSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TestIONetwork {
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
            client.closeconnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendBufferToClient(){
        byte[] sentBuffer = getRandomBufferContent();
        network.sendBufferToClient(sentBuffer);
        byte[] receivedBuffer = client.getBytesFromServer(sentBuffer.length);
        Assertions.assertArrayEquals(sentBuffer, receivedBuffer);
    }


    @Test
    public void readBufferFromClient(){
        byte[] sentBuffer = getRandomBufferContent();
        client.sendBytesToServer(sentBuffer);
        byte[] receivedBuffer = new byte[bufferSize];
        network.readBufferFromClient(receivedBuffer);
        Assertions.assertArrayEquals(sentBuffer, receivedBuffer);
    }

    @Test
    public void readByteFromClient(){
        byte[] sentByte = new byte[] {getRandomByte()};
        client.sendBytesToServer(sentByte);
        byte receiveByte = network.readByteFromClient();
        Assertions.assertEquals(sentByte[0], receiveByte);
    }

    @Test
    public void readBytesFromClient(){
        String string = "Hello World";
        byte[] stringBytes = string.getBytes();
        client.sendBytesToServer(stringBytes);
        byte[] bytes = network.readBytesFromClient(stringBytes.length);
        Assertions.assertArrayEquals(stringBytes, bytes);
    }

    @Test
    public void sendBytesToClient(){
        byte[] sentBytes = getRandomBufferContent();
        network.sendBytesToClient(sentBytes);
        byte[] receivedBytes = client.getBytesFromServer(sentBytes.length);
        Assertions.assertArrayEquals(sentBytes, receivedBytes);
    }

    @Test
    public void sendByteToClient(){
        byte sentByte = new byte[] {getRandomByte()}[0];
        network.sendByteToClient(sentByte);
        byte receivedByte = client.getBytesFromServer(1)[0];
        Assertions.assertEquals(sentByte, receivedByte);
    }

    @Test
    public void getLongFromClient(){
        Random random = new Random();
        long sentLong = random.nextLong();
        client.sendLongToServer(sentLong);
        long receivedLong = network.getLongFromClient();
        Assertions.assertEquals(sentLong, receivedLong);
    }

    @Test
    public void getSignalByteFromClient(){
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
    public void getCommandFromClient(){
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
