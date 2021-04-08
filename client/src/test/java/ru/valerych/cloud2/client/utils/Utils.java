package ru.valerych.cloud2.client.utils;

public class Utils {

    private volatile static int port = 8189;
    public synchronized static int getPort(){
        return port++;
    }
}
