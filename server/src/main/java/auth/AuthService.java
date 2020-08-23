package auth;

import main.Cloud2Server;
import main.Cloud2ServerStarter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AuthService {

    public static String AUTH_FILE = "server/src/main/resources/sfiles/auth.db";
    public static String SHARED_FILE = "server/src/main/resources/sfiles/sharedfiles.db";
    private static Path path;

    private static AuthService instance;

    private AuthService() {
        path = Paths.get(AUTH_FILE);
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public static void setAuthFile(String authFile) {
        AUTH_FILE = authFile;
    }

    public String getId(String login, String pass) throws IOException {
        Optional<String> lines = Files.lines(path)
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
        Optional<String[]> lines = Files.lines(path)
                .map((str) -> str.split(" "))
                .filter((strings) -> login.equals(strings[1]))
                .findFirst();
        return !lines.isPresent();
    }

    synchronized private String getNewUserId() throws IOException {
        Optional<String[]> lines = Files.lines(path)
                .map((str) -> str.split(" "))
                .max(Comparator.comparingInt(str -> Integer.parseInt(str[0])));
        if (lines.isPresent()) {
            int newUserId = Integer.parseInt((lines.get())[0]) + 1;
            return String.valueOf(newUserId);
        } else {
            return null;
        }
    }

    synchronized public void writeNewUserIntoDB(String userIdStr, String login, String pass) throws Exception {
        String newUserStr = "\r\n" + userIdStr + " " + login + " " + pass;
        RandomAccessFile authDBFile = new RandomAccessFile(AUTH_FILE, "rw");
        FileChannel fileChannel = authDBFile.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(Cloud2ServerStarter.BUFFER_SIZE);
        buffer.clear();
        buffer.put(newUserStr.getBytes());
        buffer.flip();
        fileChannel.position(fileChannel.size());
        while (buffer.hasRemaining()) {
            fileChannel.write(buffer);
        }
        fileChannel.close();
    }

    public File[] getSharedFiles(int userId) throws IOException {
        String id = String.valueOf(userId);
        Path sharedFilesPath = Paths.get(SHARED_FILE);
        List<String[]> fileNames = Files.lines(sharedFilesPath)
                .map((str) -> str.split(" "))
                .filter(str->str[0].equals(id)||str[0].equals("-1"))
                .collect(Collectors.toList());
        File[] files = new File[fileNames.size()];
        for (int i=0; i< files.length; i++) {
            files[i] = new File(Cloud2ServerStarter.STORAGE_ROOT_DIR + "/" + fileNames.get(i)[1] + "/" + fileNames.get(i)[2]);
        }
        return files;
    }

    public void shareFile(String nickName, String userIdStr, String fileName) throws IOException {
        String targetIdStr = getIdByNickName(nickName);
        String shareLine = "\r\n" + targetIdStr + " " + userIdStr + " " + fileName;
        System.out.println(shareLine);
        RandomAccessFile authDBFile = new RandomAccessFile(SHARED_FILE, "rw");
        FileChannel fileChannel = authDBFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(Cloud2ServerStarter.BUFFER_SIZE);
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
        Optional<String> lines = Files.lines(path)
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
