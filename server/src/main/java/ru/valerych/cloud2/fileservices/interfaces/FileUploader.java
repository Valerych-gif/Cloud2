package ru.valerych.cloud2.fileservices.interfaces;

public interface FileUploader {
    boolean writeBufferToFile(byte[] buffer);
}
