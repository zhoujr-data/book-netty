package com.book.netty.lightning;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description todo
 * @Author zhoujr
 * @Data 2025/1/9 11:22
 */
public class IOServer {
    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(10000)) {
            // 接受新连接线程
            new Thread(() -> {
                while (true) {
                    try {
                        // (1) 阻塞方法获取新连接
                        Socket socket = serverSocket.accept();
                        // (2) 为每一个新连接创建一个线程，负责读取数据
                        new Thread(() -> {
                            try {
                                int len;
                                byte[] data = new byte[1024];
                                InputStream inputStream = socket.getInputStream();
                                // (3) 按照字节流的方式读取数据
                                while ((len = inputStream.read(data)) != -1) {
                                    System.out.println(new String(data, 0, len));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
