package ru.valerych.cloud2.network.ionetwork;

import commands.Requests;
import ru.valerych.cloud2.network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class IOCommandReceiver {

    private final Network network;

    protected Logger logger = LogManager.getLogger(IOCommandReceiver.class);

    public IOCommandReceiver(Network network) {
        this.network = network;
    }

    public byte getSignalByteFromClient(){
        return network.readByteFromClient();
    }


    public Requests getCommandFromClient(){
        byte signalByte = getSignalByteFromClient();
        Requests[] commands = Requests.values();
        for (Requests c : commands) {
            if (signalByte == c.get()){
                logger.info(String.format("Got signal byte '%s' [%s] from client", String.valueOf(signalByte), c));
                return c;
            }
        }
        return null;
    }
    
}
