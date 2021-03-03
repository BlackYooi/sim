package com.black.sim.handler;

import com.black.sim.client.SimClientSession;
import com.black.sim.constant.LoginStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：客户登录响应处理器
 * @author：8568
 */
@Slf4j
public class LoginResponseHandler extends AbstractDefaultMsgHandler {

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return HeadType.LOGIN_RESPONSE.equals(message.getType());
    }

    @Override
    public void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        if (isLoginSuccess(message)) {
            ChannelPipeline pipeline = ctx.pipeline();
            // 保存会话
            SimClientSession.loginSuccess(ctx, message);
            // 在编码器后面添加心跳处理器
            pipeline.addAfter("encode", "heatBeat", new HeartBeatClientHandler());
            // 移除登录响应器
            pipeline.remove(this);
        } else {
            SimClientSession clientSession = (SimClientSession) ctx.channel().attr(SimClientSession.SESSION_KEY).get();
            clientSession.getLoginFailCount().incrementAndGet();
        }
    }

    private Boolean isLoginSuccess(DefaultMessage message) {
        LoginResponse loginResponse = message.getLoginResponse();
        LoginStatus status = LoginStatus.getByCode(loginResponse.getCode());
        log.info(String.format("登录是否成功：【%s】、描述：【%s】", status.isLogin(), status.getDesc()));
        if (status == LoginStatus.SUCCESS) {
            // 登录成功
            return true;
        } else {
            // 登录失败
            return false;
        }
    }
}
