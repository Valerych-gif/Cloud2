package ru.valerych.cloud2.client.network;

public interface ConnectionSubject {
    void registerObserver(ConnectionObserver observer);
    void removeObserver(ConnectionObserver observer);
    void notifyObservers();
}
