import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Controller implements Initializable {

    public static String DOWNLOAD_COMMAND = "./download";
    public static String CLOSE_CONNECTION_COMMAND = "./closeconnection";

    public Button send;
    public ListView<String> listView;
    public TextField text;
    private List<File> clientFileList;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private File clientDir;

    public void downloadFileAction(ActionEvent actionEvent) {
        String command = text.getCharacters().toString();
        if (command.startsWith(DOWNLOAD_COMMAND)){
            downLoadFile(command);
        }
        if (command.startsWith(CLOSE_CONNECTION_COMMAND)){
            sendCommand(CLOSE_CONNECTION_COMMAND);
        }
    }

    private void sendCommand(String command){
        try {
            os.writeUTF(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downLoadFile(String commandToDownloadFile){
        try {
            String[] commands = commandToDownloadFile.split(" ");
            String downloadCommand = commands[0];
            String downloadedFileName = commands[1];
            os.writeUTF(downloadCommand);
            os.writeUTF(downloadedFileName);
            String response = is.readUTF();
            if (response.equals("./take")){
                String downloadedFileFullName = clientDir + "/" + is.readUTF();
                System.out.println(downloadedFileFullName);
                long downloadedFileSize = is.readLong();
                File downloadedFile = new File(downloadedFileFullName);
                if (!downloadedFile.exists()) {
                    if (!downloadedFile.createNewFile());
                }
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                try {
                    System.out.println("Получение файла");
                    FileOutputStream fos = new FileOutputStream(downloadedFile);
                    for (long i = 0; i < (downloadedFileSize / bufferSize == 0 ? 1 : downloadedFileSize / bufferSize); i++) {
                        int bytesRead = is.read(buffer);
                        fos.write(buffer, 0, bytesRead);
                        fos.flush();
                    }
                    os.writeUTF("Ok");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread.sleep(1000);
            clientFileList = new ArrayList<>();
            String clientPath = "./client/src/main/resources/";
            clientDir = new File(clientPath);
            if (!clientDir.exists()) {
                throw new RuntimeException("directory resource not exists on client");
            }
            for (File file : Objects.requireNonNull(clientDir.listFiles())) {
                clientFileList.add(file);
                listView.getItems().add(file.getName());
            }
            listView.setOnMouseClicked(a -> {
                if (a.getClickCount() == 2) {
                    String fileName = listView.getSelectionModel().getSelectedItem();
                    File currentFile = findFileByName(fileName);
                    if (currentFile != null) {
                        try {
                            os.writeUTF("./upload");
                            os.writeUTF(fileName);
                            os.writeLong(currentFile.length());
                            SocketChannel bos = SocketChannel.open(new InetSocketAddress("localhost", 8190));

                            RandomAccessFile aFile = new RandomAccessFile(currentFile.getAbsolutePath(), "r");
                            FileChannel inChannel = aFile.getChannel();
                            ByteBuffer buf = ByteBuffer.allocate(1024);
                            int bytesRead = inChannel.read(buf);
                            while (bytesRead != -1) {
                                buf.flip();
//                                while(buf.hasRemaining()){
//                                    //System.out.print((char) buf.get());
//                                    byte b = buf.get();
//                                }
                                bos.write(buf);
                                buf.clear();

                                bytesRead = inChannel.read(buf);
                            }
                            aFile.close();


//                            FileInputStream fis = new FileInputStream(currentFile);

//                            byte [] buffer = new byte[1024];
//                            while (fis.available() > 0) {
//                                int bytesRead = fis.read(buffer);
//                                os.write(buffer, 0, bytesRead);
//                            }
//                            os.flush();
//                            byte[] response = new byte[1024];
//                            is.read(response);
//                            System.out.println(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File findFileByName(String fileName) {
        for (File file : clientFileList) {
            if (file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getIs() {
        return is;
    }

    public DataOutputStream getOs() {
        return os;
    }
}
