package ru.valerych.cloud2.client.entities;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileInfo {
    private SimpleStringProperty fileName;
    private SimpleStringProperty ext;
    private SimpleLongProperty size;
    private SimpleStringProperty date;
    private SimpleStringProperty attr;

    private Path path;
    private boolean isDirectory;

    public FileInfo(String fileName, String ext, long size, String date, String attr, boolean isDirectory) {
        this.fileName = new SimpleStringProperty(fileName);
        this.ext = new SimpleStringProperty(ext);
        this.size = new SimpleLongProperty(size);
        this.date = new SimpleStringProperty(date);
        this.attr = new SimpleStringProperty(attr);
        this.isDirectory = isDirectory;
    }

    public FileInfo(Path path, String fileName) {
        this.path = path;
        String fileNameStr = fileName!=null?fileName:path.getFileName().toString();
        this.fileName = new SimpleStringProperty(fileNameStr);
        getAttributes(path);
    }

    public FileInfo(Path path) {
        this.path = path;
        this.fileName = new SimpleStringProperty(path.getFileName().toString());
        getAttributes(path);
    }

    private void getAttributes(Path path) {
        BasicFileAttributes attrs = null;
        try {
            attrs = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (attrs != null) {
            this.ext = new SimpleStringProperty("ext");
            this.size = new SimpleLongProperty(attrs.size());
            String lastModifiedTimeStr = attrs.lastModifiedTime().toString();

            this.date = new SimpleStringProperty(lastModifiedTimeStr);
            this.attr = new SimpleStringProperty("attr");
            this.isDirectory = attrs.isDirectory();
        } else {
            this.ext = new SimpleStringProperty("Undefined");
            this.size = new SimpleLongProperty(0L);
            this.date = new SimpleStringProperty("Undefined");
            this.attr = new SimpleStringProperty("Undefined");
            this.isDirectory = false;
        }
    }

    public String getFileName() {
        return fileName.get();
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public String getExt() {
        return ext.get();
    }

    public SimpleStringProperty extProperty() {
        return ext;
    }

    public long getSize() {
        return size.get();
    }

    public SimpleLongProperty sizeProperty() {
        return size;
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public String getAttr() {
        return attr.get();
    }

    public SimpleStringProperty attrProperty() {
        return attr;
    }

    public Path getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
