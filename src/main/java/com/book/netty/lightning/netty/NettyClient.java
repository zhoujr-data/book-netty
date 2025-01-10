package com.book.netty.lightning.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description Netty客户端
 * @Author zhoujr
 * @Data 2025/1/9 16:44
 */
public class NettyClient {
    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap
                // 1、执行线程模型
                .group(group)
                // 2、指定IO模型为NIO
                .channel(NioSocketChannel.class)
                // 3、IO处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 客户端channel自定义属性
                .attr(AttributeKey.newInstance("clientKey"), "nettyClient")
                // TCP属性，连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // TCP属性，心跳机制开启
                .option(ChannelOption.SO_KEEPALIVE, true)
                // TCP属性，Nagle算法开启，关闭可以减少网络交互次数，数据实时性降低
                .option(ChannelOption.TCP_NODELAY, true);
        // 4、建立连接
        connect(bootstrap, "127.0.0.1", 446, MAX_RETRY);

//        Channel channel = bootstrap
//                .connect("127.0.0.1", 446)
//                .addListener(future -> {
//                    if (future.isSuccess()) {
//                        System.out.println("连接成功！");
//                    }else {
//                        System.out.println("连接失败！");
//                    }
//                })
//                .channel();
//        while (true) {
//            channel.writeAndFlush(new Date() + "：Hello World");
//            Thread.sleep(2000);
//        }
    }
    static int MAX_RETRY = 5;
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        Channel channel = bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {

                System.out.println("连接成功！");
            } else if (retry == 0) {
                System.out.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连间隔，2^N
                int delay = 1 << order;
                System.out.println(new Date() + "：连接失败，第" + order + "次重连等待" + delay + "秒.......");
                bootstrap
                        .config()
                        .group()
                        .schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        }).channel();

        while (true) {
            try {
                channel.writeAndFlush(new Date() + "：Hello World");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
