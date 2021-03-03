package com.black.sim.server;

import com.black.sim.other.ImSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class SimServerSession extends ImSession {

    public static final AttributeKey<String> KEY_USER_ID =
            AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<SimServerSession> SESSION_KEY =
            AttributeKey.valueOf("SESSION_KEY");

    /**
     * session中存储的session 变量属性值
     */
    private Map<String, Object> map = new HashMap<String, Object>();

    public SimServerSession(Channel c) {
        setChannel(c);
        setSessionId(buildNewSessionId());
    }

    public static SimServerSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(SimServerSession.SESSION_KEY).get();
    }

    public boolean isValid() {
        return getUser() != null ? true : false;
    }

    /**
     * 用户登录成功时绑定会话
    */
    public SimServerSession bind() {
        log.info(" ServerSession 绑定会话 " + getChannel().remoteAddress());
        getChannel().attr(SimServerSession.SESSION_KEY).set(this);
        SessionContext.getInstance().addSession(getSessionId(), this);
        setLogin(true);
        return this;
    }

    /**
     * sessionId生成
    */
    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public synchronized void set(String key, Object value) {
        map.put(key, value);
    }


    public synchronized <T> T get(String key) {
        return (T) map.get(key);
    }

    /**
     * 写数据包给客户
    */
    public synchronized void writeAndFlush(Object pkg) {
        getChannel().writeAndFlush(pkg);
    }

    @Override
    public synchronized void close() {
        try {
            ChannelFuture close = getChannel().close().sync();
            SessionContext.getInstance().removeSession(getSessionId());
            if (close.isSuccess()) {
                log.info("close success");
                return;
            }
        } catch (InterruptedException e) {
            log.error(String.format("关闭错误，原因【%s】", e.getMessage()));
        }
        log.error("关闭失败");
    }

    public static void close(ChannelHandlerContext ctx) {
        SimServerSession session =
                ctx.channel().attr(SimServerSession.SESSION_KEY).get();
        session.close();
    }
}
