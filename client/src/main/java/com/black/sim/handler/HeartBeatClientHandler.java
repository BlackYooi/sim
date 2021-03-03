package com.black.sim.handler;

import com.black.sim.client.SimClientSession;
import com.black.sim.configure.ClientConfig;
import com.black.sim.configure.CommonConfig;
import com.black.sim.exception.ServerCanNotAvailableException;
import com.black.sim.other.UserInfo;
import com.black.sim.potobuf.DefaultMsgBuilder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.black.sim.protobuf.DefaultMsg.notMsg;
import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.HeadType;
import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;

/**
 * @description：客户端心跳处理器
 * @author：8568
 */
@Slf4j
public class HeartBeatClientHandler extends ChannelHandlerAdapter {

    CommonConfig commonConfig = ClientConfig.getInstance();

    /**
     * 监听此处理器被添加事件、当被加入Pipeline时就开始发送心跳
    */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        SimClientSession session = SimClientSession.getSession(ctx);
        UserInfo user = session.getUser();
        DefaultMessage message = DefaultMsgBuilder.buildHearBeatMsg(session);
        // 发送心跳
        hearBeat(ctx, message);
    }

    private void hearBeat(ChannelHandlerContext ctx, DefaultMessage message) {
        ctx.executor().scheduleAtFixedRate(() -> {
            if (ctx.channel().isActive()) {
                log.info("❤ping❤");
                ChannelFuture channelFuture = ctx.writeAndFlush(message);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            throw new ServerCanNotAvailableException();
                        }
                    }
                });
            }
        }, commonConfig.getHeartBeatInterval(), commonConfig.getHeartBeatInterval(), TimeUnit.SECONDS);
    }

    /**
     * 回显服务器心跳
    */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (notMsg(msg)) {
            super.channelRead(ctx, msg);
            return;
        }
        DefaultMessage message = (DefaultMessage) msg;
        HeadType type = message.getType();
        if (type.equals(HeadType.KEEPALIVE_RESPONSE)) {
            log.info("❤pong❤");
            return;
        } else {
            // 交到下一站处理
            super.channelRead(ctx, msg);
        }
    }
}
