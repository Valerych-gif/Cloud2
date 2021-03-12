package ru.valerych.cloud2.entities;

public class FileInfo {

    public enum Type {
        DIRECTORY((byte)'D'),
        FILE ((byte)'F');

        private byte mark;

        Type(byte mark) {
            this.mark = mark;
        }

        public byte getMark() {
            return mark;
        }
    }

    private String fileName;
    private long fileSize;
    private Type type;

    public FileInfo(String fileName, long fileSize, Type type) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Type getType() {
        return type;
    }


}
