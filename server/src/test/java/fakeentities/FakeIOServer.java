//package fakeentities;
//
//import servers.Cloud2Server;
//import connectionhandlers.IOConnectionHandler;
//import settings.Cloud2ServerSettings;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.ServerSocket;
//
//public class FakeIOServer extends Cloud2Server {
//
//    private IOConnectionHandler connectionHandler;
//    ServerSocket serverSocket;
//    private static Cloud2Server instance;
//
//    private FakeIOServer() {
//        storage = new File("src/test/resources/storage");
//    }
//
//    public static Cloud2Server getInstance() {
//        if (instance == null) {
//            Cloud2Server.setServer(new FakeIOServer());
//            instance = Cloud2Server.getInstance();
//        }
//        return instance;
//    }
//
//    public void waitConnection() {
//        try {
//            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
//            socket = serverSocket.accept();
//            connectionHandler = new IOConnectionHandler(socket);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public IOConnectionHandler getConnectionHandler() {
//        return connectionHandler;
//    }
//
//    @Override
//    public File getStorage() {
//        return storage;
//    }
//
//    public void closeConnection(){
//        try {
//            socket.close();
//            serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        connectionHandler=null;
//    }
//}
