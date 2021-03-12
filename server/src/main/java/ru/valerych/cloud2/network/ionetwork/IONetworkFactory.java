package ru.valerych.cloud2.network.ionetwork;

import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;

import java.net.Socket;

public class IONetworkFactory implements NetworkFactory {
    @Override
    public Network createNetwork(Socket socket) {
        return new IONetwork(socket);
    }
}
