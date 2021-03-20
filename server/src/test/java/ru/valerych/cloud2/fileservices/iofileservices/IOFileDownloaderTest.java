package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static ru.valerych.cloud2.settings.Cloud2ServerSettings.BUFFER_SIZE;
import static ru.valerych.cloud2.testutils.ServerFileStructureUtils.*;

class IOFileDownloaderTest {

    @AfterAll
    static void removeFile(){
        removeUserFile();
    }

    @Test
    @DisplayName("Reading buffer from file is success")
    void readBufferFromFileSuccessTest() throws IOException {
        File userFile = createFilledUserFile();
        Assertions.assertTrue(userFile.exists());
        IOFileDownloader ioFileDownloader = new IOFileDownloader(userFile);
        byte[] buffer = new byte[BUFFER_SIZE];
        ioFileDownloader.readBufferFromFile(buffer);

        Assertions.assertTrue(new String(buffer).contains("Test line for writing to userFile.txt"));
        ioFileDownloader.closeFile();
    }

    @Test
    @DisplayName("Reading buffer from file is fail (file is empty)")
    void readBufferFromFileFailTest() {
        File userFile = createUserFile();
        Assertions.assertTrue(userFile.exists());
        IOFileDownloader ioFileDownloader = new IOFileDownloader(userFile);
        byte[] buffer = new byte[BUFFER_SIZE];

        Assertions.assertThrows(IOException.class, ()->ioFileDownloader.readBufferFromFile(buffer));
        ioFileDownloader.closeFile();
    }

    @Test
    @DisplayName("Reading bytes from file is success")
    void readBytesFromFileSuccessTest() throws IOException {
        File userFile = createFilledUserFile();
        Assertions.assertTrue(userFile.exists());
        IOFileDownloader ioFileDownloader = new IOFileDownloader(userFile);
        byte[] bytes = ioFileDownloader.readBytesFromFile(9);
        Assertions.assertEquals(new String(bytes), "Test line");
        ioFileDownloader.closeFile();
    }

    @Test
    @DisplayName("Reading bytes from file is fail (file is empty)")
    void readBytesFromFileFailTest() {
        File userFile = createUserFile();
        Assertions.assertTrue(userFile.exists());
        IOFileDownloader ioFileDownloader = new IOFileDownloader(userFile);

        Assertions.assertThrows(IOException.class, ()->ioFileDownloader.readBytesFromFile(9));
        ioFileDownloader.closeFile();
    }

}