package fakeentities;

import java.io.*;
import java.net.Socket;

public class FakeClient {
    private final String PATH_FOR_TEST_FILE = "src/test/resources/fakeClientFiles/";
    private final String TEST_FILE1_NAME = "testFile1.txt";
    private final String TEST_FILE2_NAME = "testFile2.txt";

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private File file1;
    private File file2;

    public FakeClient() {
        file1 = new File(PATH_FOR_TEST_FILE + TEST_FILE1_NAME);
        file2 = new File(PATH_FOR_TEST_FILE + TEST_FILE2_NAME);
    }

    public void connect() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                socket = new Socket("localhost", 8189);
                is = new DataInputStream(socket.getInputStream());
                os = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }


    public void sendFiles() {
        new Thread(() -> {
            String fileName1 = PATH_FOR_TEST_FILE + TEST_FILE1_NAME;
            File currentFile1 = new File(fileName1);
            sendFile(currentFile1);
            String fileName2 = PATH_FOR_TEST_FILE + TEST_FILE2_NAME;
            File currentFile2 = new File(fileName2);
            sendFile(currentFile2);
            try {
                Thread.sleep(1000);
                os.writeUTF("./closeconnection");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendFile(File currentFile) {

        if (currentFile != null) {
            try {
                Thread.sleep(500);
                os.writeBytes("./upload");
                if (getResponse()) {
                    os.writeBytes(currentFile.getName());
                }
                if (getResponse()) {
                    long fileLength = currentFile.length();
                    os.writeLong(fileLength);
                }

                if (getResponse()) {
                    sendFile(currentFile);
                }

                FileInputStream fis = new FileInputStream(currentFile);

                byte [] buffer = new byte[1024];
                while (fis.available() > 0) {
                    int bytesRead = fis.read(buffer);
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean getResponse() {
        StringBuilder command = new StringBuilder();
        try {
            for (int i = 0; i < 100; i++) {
                byte b = is.readByte();
                command.append((char)b);
                if (command.toString().equals("./ok")){
                    return true;
                }
                if (command.toString().equals("./fail")){
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendCommand(String command) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                os.writeBytes(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public File getFile1() {
        return file1;
    }

    public File getFile2() {
        return file2;
    }
}
