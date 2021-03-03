package com.black.sim.other;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description：通讯框架的会话
 * @author：8568
 */
@Data
public abstract class ImSession {

    public static final AttributeKey<ImSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    /**
     * 通道和用户
     */
    private Channel channel;

    private UserInfo user;

    /**
     * 登录过后的sessionId
     */
    protected String sessionId;

    /**
     * 是否建立连接（不代表已经登录）
     */
    private boolean isConnected = false;

    /**
     * 是否登录
     */
    private boolean isLogin = false;

    /**
     * 记录登录失败次数
    */
    private AtomicInteger loginFailCount = new AtomicInteger(0);

    /**
     * Description: 关闭seesion
     *
     * @param
     * @return: void
    */
    public abstract void close();
}
