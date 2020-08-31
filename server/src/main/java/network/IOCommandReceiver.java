package network;

import commands.Commands;
import connectionhandlers.ConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class IOCommandReceiver {

    private DataInputStream is;

    protected Logger logger = LogManager.getLogger(ConnectionHandler.class);

    public IOCommandReceiver(Socket socket) {
        try {
            this.is = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
    }

    public byte getSignalByteFromClient() throws IOException {
        byte signalByte = is.readByte();
        LogUtils.info(String.valueOf(signalByte), logger, "<-\t");
        return signalByte;
    }

    public String getStringFromClient() throws IOException {
        StringBuilder stringFromClient = new StringBuilder();
        char b = 0;

        while (true) {
            b = (char) is.readByte();
            if (b != Cloud2ServerSettings.END_COMMAND_CHAR) {
                stringFromClient.append(b);
            } else {
                System.out.println("<-\t" + stringFromClient.toString());
                return stringFromClient.toString();
            }
        }
    }
}
