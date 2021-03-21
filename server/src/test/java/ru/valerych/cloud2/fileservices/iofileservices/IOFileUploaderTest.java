package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static ru.valerych.cloud2.testutils.ServerFileStructureUtils.createUserFile;
import static ru.valerych.cloud2.testutils.ServerFileStructureUtils.removeUserFile;

class IOFileUploaderTest {

    private final String FILE_CONTENT = "Test line to write to userFile.txt\n";

    @AfterAll
    static void removeFile(){
        removeUserFile();
    }

    @Test
    @DisplayName("Writing of buffer to file is success")
    void writeBufferToFileSuccessTest() {
        File userFile = createUserFile();
        byte[] buffer = FILE_CONTENT.getBytes(StandardCharsets.UTF_8);
        IOFileUploader fileUploader = new IOFileUploader(userFile);
        fileUploader.writeBufferToFile(buffer);
        Assertions.assertEquals(FILE_CONTENT.length(), userFile.length());
        fileUploader.closeFile();
    }
}