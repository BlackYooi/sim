package com.black.sim.client;

import com.black.sim.other.ImSession;
import com.black.sim.other.UserInfo;
import com.black.sim.protobuf.BaseMsg;
import com.black.sim.protobuf.DefaultProtoMsg;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @description：客户端的会话
 * @author：8568
 */
@Slf4j
public class SimClientSession extends ImSession {

    /**
     * 变量属性值
    */
    private Map<String, Object> map = new HashMap<>();

    /**
     * 绑定通道的构造函数
    */
    public SimClientSession(Channel channel) {
        setChannel(channel);
        this.sessionId = String.valueOf("-1");
        channel.attr(SimClientSession.SESSION_KEY).set(this);
    }

    /**
     * 登录成功设置sessionId
    */
    public static void loginSuccess(ChannelHandlerContext ctx, DefaultProtoMsg.ProtoMsg.DefaultMessage message) {
        Channel channel = ctx.channel();
        SimClientSession clientSession = (SimClientSession) channel.attr(SimClientSession.SESSION_KEY).get();
        clientSession.setSessionId(message.getSessionId());
        clientSession.setLogin(true);
    }

    /**
     * 获取会话
    */
    public static SimClientSession getSession(ChannelHandlerContext ctx) {
        return (SimClientSession) ctx.channel().attr(SimClientSession.SESSION_KEY).get();
    }

    /**
     * 关闭
    */
    @Override
    public void close() {
        super.setConnected(false);
        super.setLogin(false);
        ChannelFuture close = getChannel().close();
        close.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (close.isSuccess()) {
                    log.info("close success");
                }
            }
        });
    }
}
