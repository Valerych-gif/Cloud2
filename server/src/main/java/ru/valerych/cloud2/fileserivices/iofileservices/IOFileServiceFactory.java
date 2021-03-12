package ru.valerych.cloud2.fileserivices.iofileservices;

import ru.valerych.cloud2.fileserivices.interfaces.FileService;
import ru.valerych.cloud2.fileserivices.interfaces.FileServiceFactory;
import ru.valerych.cloud2.fileserivices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;

public class IOFileServiceFactory implements FileServiceFactory {
    @Override
    public FileService createFileService(Network network, ServerFileExplorer serverFileExplorer) {
        return new IOFileService(network, serverFileExplorer);
    }
}