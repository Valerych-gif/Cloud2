package services;

import settings.Cloud2ServerSettings;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthService {

    private static Path authFilePath;
    private static Path sharedFilePath;

    private static AuthService instance;

    private AuthService() {
        authFilePath = Paths.get(
                Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
                Cloud2ServerSettings.AUTH_FILE
        );
        sharedFilePath = Paths.get(
                Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
                Cloud2ServerSettings.AUTH_FILE
        );
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public String getId(String login, String pass) throws IOException {
        Optional<String> lines = Files.lines(authFilePath)
                .filter((str) -> {
                    String[] line = str.split(" ");
                    return login.equals(line[1]) && pass.equals(line[2]);
                })
                .findFirst();
        String userIdStr = null;
        if (lines.isPresent()) {
            userIdStr = (lines.get().split(" "))[0];
        }
        return userIdStr;
    }

    public String registration(String login, String pass) throws Exception {
        String userIdStr = getId(login, pass);
        if (userIdStr == null) {
            userIdStr = getNewUserId();
            if (isLoginFree(login)) {
                writeNewUserIntoDB(userIdStr, login, pass);
            } else {
                return null;
            }
        }
        return userIdStr;
    }

    synchronized private boolean isLoginFree(String login) throws IOException {
        Optional<String[]> lines = Files.lines(authFilePath)
                .map((str) -> str.split(" "))
                .filter((strings) -> login.equals(strings[1]))
                .findFirst();
        return !lines.isPresent();
    }

    synchronized private String getNewUserId() throws IOException {
        Optional<String[]> lines = Files.lines(authFilePath)
                .map((str) -> str.split(" "))
                .max(Comparator.comparingInt(str -> Integer.parseInt(str[0])));
        if (lines.isPresent()) {
            int newUserId = Integer.parseInt((lines.get())[0]) + 1;
            return String.valueOf(newUserId);
        } else {
            return "0";
        }
    }

    synchronized public void writeNewUserIntoDB(String userIdStr, String login, String pass) throws Exception {
        String newUserStr = userIdStr + " " + login + " " + pass + "\r\n";
        Files.write(authFilePath, newUserStr.getBytes(), StandardOpenOption.APPEND);
    }

    public File[] getSharedFiles(int userId) throws IOException {
        String id = String.valueOf(userId);
        List<String[]> fileNames = Files.lines(sharedFilePath)
                .map((str) -> str.split(" "))
                .filter(str->str[0].equals(id)||str[0].equals("-1"))
                .collect(Collectors.toList());
        File[] files = new File[fileNames.size()];
        for (int i=0; i< files.length; i++) {
            files[i] = new File(Cloud2ServerSettings.STORAGE_ROOT_DIR + "/" + fileNames.get(i)[1] + "/" + fileNames.get(i)[2]);
        }
        return files;
    }

    public void shareFile(String nickName, String userIdStr, String fileName) throws IOException {
        String targetIdStr = getIdByNickName(nickName);
        String shareLine = "\r\n" + targetIdStr + " " + userIdStr + " " + fileName;
        System.out.println(shareLine);
        RandomAccessFile authDBFile = new RandomAccessFile(String.valueOf(sharedFilePath), "rw");
        FileChannel fileChannel = authDBFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(Cloud2ServerSettings.BUFFER_SIZE);
        buffer.clear();
        buffer.put(shareLine.getBytes());
        buffer.flip();
        fileChannel.position(fileChannel.size());
        while (buffer.hasRemaining()) {
            fileChannel.write(buffer);
        }
        fileChannel.close();
    }

    private String getIdByNickName(String login) throws IOException {
        Optional<String> lines = Files.lines(authFilePath)
                .filter((str) -> {
                    String[] line = str.split(" ");
                    return login.equals(line[1]);
                })
                .findFirst();
        String userIdStr = null;
        if (lines.isPresent()) {
            userIdStr = (lines.get().split(" "))[0];
        }
        return userIdStr;
    }
}
