package ru.valerych.cloud2.fileserivices.interfaces;

import ru.valerych.cloud2.network.interfaces.Network;

public interface FileServiceFactory {
    FileService createFileService(Network network, ServerFileExplorer serverFileExplorer);
}
