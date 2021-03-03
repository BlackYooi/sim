package com.black.sim.handler;

import com.black.sim.codec.DefaultMsgDecoder;
import com.black.sim.codec.DefaultMsgEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @description：默认的通道、适用于默认的通讯歇息
 * @author：8568
 */
public class DefaultClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 解码器
        ch.pipeline().addLast("decoder", new DefaultMsgDecoder());
        // 编码器
        ch.pipeline().addLast("encode", new DefaultMsgEncoder());
        // 登录响应处理器
        ch.pipeline().addLast(new LoginResponseHandler());
        // 消息处理器
        ch.pipeline().addLast(new DefaultMsgHandler());
        // 异常处理器
        ch.pipeline().addLast(new ImExceptionHandler());
    }
}
