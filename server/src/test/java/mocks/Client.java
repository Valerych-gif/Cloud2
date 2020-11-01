package mocks;

import settings.Cloud2ServerSettings;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public Client() {
        try {
            socket = new Socket("localhost", Cloud2ServerSettings.PORT);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytesFromServer(int length){
        try {
            byte[] bytes = new byte[length];
            Thread.sleep(100);
            is.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendBytesToServer(byte[] bytes){
        try {
            os.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLongToServer(long l){
        try {
            os.writeLong(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getLong() {
        long l = 0;
        try {
            l = is.readLong();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }

    public void closeconnection() {
        try {
            is.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
