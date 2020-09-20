package fileserivices.interfaces;

public interface FileService {
    boolean receiveFileFromClient();
    boolean sendDirContent();

    boolean sendFileToClient();
}
