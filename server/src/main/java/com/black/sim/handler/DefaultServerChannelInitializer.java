package com.black.sim.handler;

import com.black.sim.codec.DefaultMsgDecoder;
import com.black.sim.codec.DefaultMsgEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @description：
 * @author：8568
 */
public class DefaultServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    LoginRequestHandler loginRequestHandler = new LoginRequestHandler();
    ChatHandler chatHandler = new ChatHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 解码器
        ch.pipeline().addLast("decoder", new DefaultMsgDecoder());
        // 编码器
        ch.pipeline().addLast("encode", new DefaultMsgEncoder());
        // 心跳
        ch.pipeline().addLast("heartBeat", new HearBeatServerHandler());
        // 处理登录
        ch.pipeline().addLast(loginRequestHandler);
        // 消息处理
        ch.pipeline().addLast(this.chatHandler);
        // 异常处理
        ch.pipeline().addLast(new ImExceptionHandler());
    }
}
