package com.book.netty.lightning.io;

import java.net.Socket;
import java.util.Date;

/**
 * @Description IO流客户端
 * @Author zhoujr
 * @Data 2025/1/9 13:51
 */
public class IOClient {
    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try (Socket socket = new Socket("127.0.0.1", 8000)) {
                while (true) {
                    try {
                        socket.getOutputStream().write((new Date() + "：hello world!").getBytes());
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
