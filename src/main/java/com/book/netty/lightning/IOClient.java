package com.book.netty.lightning;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Description todo
 * @Author zhoujr
 * @Data 2025/1/9 13:51
 */
public class IOClient {
    public static void main(String[] args) {
        new Thread(() -> {
            try (Socket socket = new Socket("127.0.0.1", 10000)) {
                while (true) {
                    // 2秒执行一次
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                socket.getOutputStream().write((new Date() + "：hello world!").getBytes());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, 1000L * 2, 1000L * 2);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();


    }
}
