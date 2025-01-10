package com.book.netty.lightning.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @Description 逻辑处理器
 * @Author zhoujr
 * @Data 2025/1/10 14:21
 */
public class FirstClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(new Date() + "：客户端读到数据 -> " + buf.toString(StandardCharsets.UTF_8));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ": 客户端写出数据");
        // 1、获取数据
        ByteBuf buffer = getByteBuf(ctx);
        // 2、 写数据
        ctx.writeAndFlush(buffer);
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        // 1、获取二进制抽象byteBuf
        ByteBuf buffer = ctx.alloc().buffer();
        // 2、准备数据，指定字符串的字符集为 UTF-8
        byte[] bytes = "意大利诗人莫泊桑说过，生活没有你想的那好，但也没有那么糟。".getBytes(StandardCharsets.UTF_8);
        // 3、填充数据到byteBuf
        buffer.writeBytes(bytes);
        return buffer;
    }
}
