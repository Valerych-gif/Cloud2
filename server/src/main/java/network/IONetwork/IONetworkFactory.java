package network.IONetwork;

import network.interfaces.Network;
import network.interfaces.NetworkFactory;

import java.net.Socket;

public class IONetworkFactory implements NetworkFactory {
    @Override
    public Network createNetwork(Socket socket) {
        return new IONetwork(socket);
    }
}
