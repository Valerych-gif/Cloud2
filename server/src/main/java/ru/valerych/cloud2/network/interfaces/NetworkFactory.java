package ru.valerych.cloud2.network.interfaces;

import java.net.Socket;

public interface NetworkFactory {
    Network createNetwork(Socket socket);
}
