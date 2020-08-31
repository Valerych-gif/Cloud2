//package utils;
//
//import fakeentities.FakeClient;
//import fakeentities.FakeIOServer;
//import servers.Cloud2Server;
//import connectionhandlers.ConnectionHandler;
//import main.iotests.TestIOConnectionHandler;
//
//import java.io.File;
//
//public class Utils {
//
//    public static void clearStorage(File testStorage) {
//        Utils.recursiveDelete(testStorage);
//        if (!testStorage.exists()){
//            testStorage.mkdir();
//        }
//    }
//
//    public static void recursiveDelete(File file) {
//        if (!file.exists())
//            return;
//
//        if (file.isDirectory()) {
//            for (File f : file.listFiles()) {
//                recursiveDelete(f);
//            }
//        }
//
//        file.delete();
//    }
//
//    public static void startServer(){
//        new Thread(()->{
//            Cloud2Server testServer = FakeIOServer.getInstance();
//            TestIOConnectionHandler.setTestServer(testServer);
//            testServer.waitConnection();
//            ConnectionHandler connectionHandler = testServer.getConnectionHandler();
//            TestIOConnectionHandler.setConnectionHandler(connectionHandler);
//            connectionHandler.run();
//        }).start();
//    }
//
//    public static void createClient() {
//        new Thread(()-> {
//            FakeClient client = new FakeClient();
//            client.connect();
//            try {
//                Thread.sleep(500);
//                client.auth();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            TestIOConnectionHandler.setClient(client);
//        }).start();
//    }
//}
