package ru.valerych.cloud2.network.ionetwork;

import commands.Requests;
import ru.valerych.cloud2.network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.utils.LogUtils;


public class IOCommandReceiver {

    Network network;

    protected Logger logger = LogManager.getLogger(IOCommandReceiver.class);

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
    
}
