package com.black.sim.handler;

import com.black.sim.exception.NotLoginException;
import com.black.sim.protobuf.DefaultProtoMsg;
import com.black.sim.server.SessionContext;
import com.black.sim.server.SimServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.HeadType;

/**
 * @description：
 * @author：8568
 */
@ChannelHandler.Sharable
public class ChatHandler extends AbstractDefaultMsgHandler {

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return HeadType.MESSAGE_REQUEST.equals(message.getType());
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        msgRequestConsumer(ctx, message);
    }

    private void msgRequestConsumer(ChannelHandlerContext ctx, DefaultMessage m) throws Exception {
        // 是否登录
        SimServerSession session = SimServerSession.getSession(ctx);
        if (null == session || !session.isLogin()) {
            throw new NotLoginException();
        }
        // 处理消息
        ctx.executor().execute(() -> processMsg(m));
    }

    private void processMsg(DefaultMessage msg) {
        DefaultProtoMsg.ProtoMsg.MessageRequest messageRequest = msg.getMessageRequest();
        String toUser = messageRequest.getTo();
        List<SimServerSession> toSessions = SessionContext.getInstance().getSessionsBy(toUser);
        if (null == toSessions || toSessions.isEmpty()) {
            // todo 推到消息中兴
        } else {
            toSessions.forEach(s -> s.writeAndFlush(msg));
        }
    }
}
