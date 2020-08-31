package commands;

public enum Commands {
    UPLOAD ((byte)10),
    DOWNLOAD ((byte)20),
    DELETE ((byte)30),
    CLOSE_CONNECTION((byte)40),
    GET_DIR_CONTENT((byte)50),
    GET_SHARED_DIR_CONTENT((byte)60),
    AUTHORIZATION ((byte)70),
    REGISTRATION ((byte)80),
    SHARE ((byte)90);

    Byte signalByte;

    Commands(byte signalByte) {
        this.signalByte = signalByte;
    }

    public byte get(){
        return signalByte;
    }

    @Override
    public String toString() {
        return String.valueOf(signalByte);
    }
}
