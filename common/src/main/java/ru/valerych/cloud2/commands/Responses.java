package ru.valerych.cloud2.commands;

public enum Responses {
    OK ((byte)10),
    FAIL ((byte)20),
    SEND_FILE_INFO ((byte)30),
    END_OF_DIR_CONTENT ((byte)40),
    LOGIN_ALREADY_IN_USE ((byte)50);

    private final byte signalByte;

    Responses(byte signalByte) {
        this.signalByte = signalByte;
    }

    public byte getSignalByte() {
        return signalByte;
    }
}