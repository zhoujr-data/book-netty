package com.book.netty.lightning.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.AttributeKey;

/**
 * @Description Netty服务端
 * @Author zhoujr
 * @Data 2025/1/9 15:54
 */
public class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel socketChannel) throws Exception {
//                        System.out.println("启动过程中的逻辑，通常情况用不到！");
//                    }
//                })
                // 服务端channel自定义属性，通常用不到
                .attr(AttributeKey.newInstance("nettyName"), "nettyServer")
                // 每一个连接自定义属性
                .childAttr(AttributeKey.newInstance("clientKey"), "clientValue")
                // 服务端channel设置TCP参数，表示系统用于存放已经完成三次握手队列的最大长度
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 每一个连接设置一些TCP参数
                // 是否开启TCP底层心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 是否开启nagle算法，数据实时性高，关闭，减少网络交互，开启
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 新连接的读写处理
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                String threadName = Thread.currentThread().getName();
                                System.out.println(threadName + "：" + msg);
                            }
                        });
                    }
                });
//                .bind(8000).addListener(future -> {
//                    if (future.isSuccess()) {
//                        System.out.println("端口绑定成功！");
//                    } else {
//                        System.out.println("端口绑定失败！");
//                    }
//                });
        bind(serverBootstrap, 445);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("端口" + port + "绑定成功！");
            } else {
                System.out.println("端口" + port + "绑定失败！");
                bind(serverBootstrap, port + 1);
            }
        });
    }
}
