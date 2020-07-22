import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private Logger logger = LogManager.getLogger(Cloud2ServerStarter.class);

    private DataInputStream is;
    private DataOutputStream os;

    public ConnectionHandler(Socket socket) throws IOException, InterruptedException {
        logger.info("Connection accepted");
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        //Thread.sleep(2000);
    }



    @Override
    public void run() {
        byte [] buffer = new byte[Cloud2ServerStarter.BUFFER_SIZE];
        while (true) {
            try {
                String command = is.readUTF();
                if (command.equals("./upload")) {
                    String fileName = is.readUTF();
                    System.out.println("fileName: " + fileName);
                    long fileLength = is.readLong();
                    System.out.println("fileLength: " + fileLength);
                    File file = new File(Cloud2ServerStarter.STORAGE_ROOT_DIR + "/" + fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try(FileOutputStream fos = new FileOutputStream(file)) {
                        for (long i = 0; i < (fileLength / Cloud2ServerStarter.BUFFER_SIZE == 0 ? 1 : fileLength / Cloud2ServerStarter.BUFFER_SIZE); i++) {
                            int bytesRead = is.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    os.writeUTF("OK");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
