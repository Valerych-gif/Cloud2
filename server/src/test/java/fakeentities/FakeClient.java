package fakeentities;

import main.Cloud2ServerStarter;
import main.Commands;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
                Thread.sleep(1000);
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
            try {
                String fileName1 = PATH_FOR_TEST_FILE + TEST_FILE1_NAME;
                File currentFile1 = new File(fileName1);
                sendFile(currentFile1);
                String fileName2 = PATH_FOR_TEST_FILE + TEST_FILE2_NAME;
                File currentFile2 = new File(fileName2);
                sendFile(currentFile2);
                Thread.sleep(1000);
                os.writeBytes("./closeconnection" + Cloud2ServerStarter.END_COMMAND_CHAR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendFile(File currentFile) {

        if (currentFile != null) {
            try {
                Thread.sleep(500);
                os.writeBytes(Commands.UPLOAD.getString()+ Cloud2ServerStarter.END_COMMAND_CHAR);
                os.flush();
                if (getResponse()) {
                    os.writeBytes(currentFile.getName()+ Cloud2ServerStarter.END_COMMAND_CHAR);
                    os.flush();
                }
                if (getResponse()) {
                    long fileLength = currentFile.length();
                    String fileLengthStr = String.valueOf(fileLength);
                    os.writeBytes(fileLengthStr + Cloud2ServerStarter.END_COMMAND_CHAR);
                    os.flush();
                }

                if (getResponse()) {
                    FileInputStream fis = new FileInputStream(currentFile);

                    byte[] buffer = new byte[1024];
                    while (fis.available() > 0) {
                        int bytesRead = fis.read(buffer);
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean getResponse() {
        String command = getStringFromServer();
        try {
            if (command.equals("./ok")) {
                return true;
            }
            if (command.equals("./fail")) { ;
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getStringFromServer() {
        StringBuilder stringFromServer = new StringBuilder();
        char b = 0;
        try {
            while (true) {
                b = (char) is.readByte();
                if (b != Cloud2ServerStarter.END_COMMAND_CHAR) {
                    stringFromServer.append(b);
                } else {
                    return stringFromServer.toString();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringFromServer.toString();
    }

    public void sendCommand(String command) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
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
