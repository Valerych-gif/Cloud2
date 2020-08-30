package fakeentities;

import main.Cloud2ServerApp;
import main.Commands;
import main.Responses;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class FakeClient {
    private final String PATH_FOR_TEST_FILE = "src/test/resources/fakeClientFiles/";

    private final Map<String, File> clientFiles;

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public FakeClient() {
        clientFiles = new HashMap<>();
        clientFiles.put("Text file 1", new File(PATH_FOR_TEST_FILE + "testFile1.txt"));
        clientFiles.put("Text file 2", new File(PATH_FOR_TEST_FILE + "testFile2.txt"));
        clientFiles.put("Big file", new File(PATH_FOR_TEST_FILE + "testFile3.txt"));
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void auth(){
        sendCommand(Commands.AUTHORIZATION.toString());
        if (isResponseOk()) {
            sendCommand("test test");
        }
    }

    public void disconnect() {
        try {
            sendCommand(Commands.CLOSE_CONNECTION.toString());
            socket.close();
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File currentFile) {

        if (currentFile != null) {
            try {
                Thread.sleep(500);
                sendCommand(Commands.UPLOAD.getString());
                if (isResponseOk()) {
                    sendCommand(currentFile.getName());
                }
                if (isResponseOk()) {
                    long fileLength = currentFile.length();
                    String fileLengthStr = String.valueOf(fileLength);
                    sendCommand(fileLengthStr);
                }

                if (isResponseOk()) {
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

    private boolean isResponseOk() {
        String response = getStringFromServer();
        try {
            if (response.equals(Responses.OK.getString())) {
                return true;
            }
            if (response.equals(Responses.FAIL.getString())) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getStringFromServer() {
        StringBuilder stringFromServer = new StringBuilder();
        char b;
        try {
            while (true) {
                b = (char) is.readByte();
                if (b != Cloud2ServerApp.END_COMMAND_CHAR) {
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
        try {
            os.writeBytes(command + Cloud2ServerApp.END_COMMAND_CHAR);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, File> getClientFiles() {
        return clientFiles;
    }
}
