package ru.valerych.cloud2.commands;

public enum Responses {
    OK ((byte)100),
    FAIL ((byte)110),
    SEND_FILE_INFO ((byte)120),
    END_OF_DIR_CONTENT ((byte)130),
    LOGIN_ALREADY_IN_USE ((byte)140);

    private final byte signalByte;

    Responses(byte signalByte) {
        this.signalByte = signalByte;
    }

    public byte getSignalByte() {
        return signalByte;
    }
}