package ru.valerych.cloud2.fileservices.iofileservices;

import ru.valerych.cloud2.fileservices.interfaces.FileService;
import ru.valerych.cloud2.fileservices.interfaces.FileServiceFactory;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;

public class IOFileServiceFactory implements FileServiceFactory {
    @Override
    public FileService createFileService(Network network, ServerFileExplorer serverFileExplorer) {
        return new IOFileService(network, serverFileExplorer);
    }
}