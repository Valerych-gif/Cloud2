package network.IONetwork;

import commands.Requests;
import connectionhandlers.ConnectionHandler;
import network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;


public class IOCommandReceiver {

    Network network;

    protected Logger logger = LogManager.getLogger(ConnectionHandler.class);

    public IOCommandReceiver(Network network) {
        this.network = network;
    }

    public byte getSignalByteFromClient(){
        byte signalByte = network.readByteFromClient();
        LogUtils.info(String.valueOf(signalByte), logger, "<-\t");
        return signalByte;
    }


    public Requests getCommandFromClient(){
        byte signalByte = getSignalByteFromClient();
        Requests[] commands = Requests.values();
        for (Requests c : commands) {
            if (signalByte == c.get()) return c;
        }
        return null;
    }

//    public String getStringFromClient() throws IOException {
//        StringBuilder stringFromClient = new StringBuilder();
//        char b = 0;
//
//        while (true) {
//            b = (char) network.readByteFromClient();
//            if (b != Cloud2ServerSettings.END_COMMAND_CHAR) {
//                stringFromClient.append(b);
//            } else {
//                System.out.println("<-\t" + stringFromClient.toString());
//                return stringFromClient.toString();
//            }
//        }
//    }
}
