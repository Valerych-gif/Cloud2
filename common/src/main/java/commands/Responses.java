package commands;

public enum Responses {
    OK ((byte)100),
    FAIL ((byte)110),
    END_OF_DIR_CONTENT((byte)120),
    LOGIN_ALREADY_IN_USE ((byte)130);

    private byte signalByte;

    Responses(byte signalByte) {
        this.signalByte = signalByte;
    }

    public byte get() {
        return signalByte;
    }
}