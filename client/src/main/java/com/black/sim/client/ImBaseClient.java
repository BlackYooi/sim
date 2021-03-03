package com.black.sim.client;

import com.black.sim.other.ImSession;
import com.black.sim.other.UserInfo;
import com.black.sim.potobuf.DefaultMsgBuilder;
import com.black.sim.protobuf.DefaultProtoMsg;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;

/**
 * @description：
 * @author：8568
 */
public abstract class ImBaseClient implements AutoCloseable {

    /**
     * 保存了连接的会话
    */
    @Getter
    @Setter
    protected ImSession session;

    /**
     * Description: 连接到服务器
     *
     * @param
     * @return: void
    */
    protected abstract boolean connectToServer();

    /**
     * Description: 同步登录服务器
     *
     * @param u 登录信息
     * @return: boolean
    */
    public boolean login(UserInfo u) {
        boolean result = false;
        if (connectToServer()) {
            result = doLogin(u);
        }
        if (false == result) {
            getSession().close();
        }
        return result;
    }

    private boolean doLogin(UserInfo u) {
        boolean loginSuccess = false;
        session.setUser(u);
        DefaultMessage loginMsg = DefaultMsgBuilder.ofLoginMsg(session);
        int failCount = session.getLoginFailCount().intValue();
        if (sendMsgSync(loginMsg)) {
            // 发送登录消息成功时
            for (int i = 0; i < 10; i++) {
                // 尝试10次、每次失败休眠1秒
                if (session.isLogin()) {
                    return true;
                } else if (session.getLoginFailCount().intValue() != failCount) {
                    // 收到了登录失败的响应包
                    return false;
                } else {
                    Try.run(() -> TimeUnit.SECONDS.sleep(1));
                }
            }
        }
        if (session.getLoginFailCount().intValue() == failCount) {
            session.getLoginFailCount().incrementAndGet();
        }
        return loginSuccess;
    }

    /**
     * Description: 异步向服务器发送消息、必须实现自己的回调逻辑
     *
     * @param message
     * @return:
    */
    abstract void sendMsg(DefaultMessage message, ChannelFutureListener f);

    /**
     * Description: 同步方式发送消息给服务器
     *
     * @param message
     * @return: java.lang.Boolean
    */
    abstract boolean sendMsgSync(DefaultMessage message);

    /**
     * Description: 关闭客户端
     *
     * @param
     * @return: void
    */
    abstract void closeClient() throws Exception;

    @Override
    public void close() throws Exception {
        closeClient();
    }
}
