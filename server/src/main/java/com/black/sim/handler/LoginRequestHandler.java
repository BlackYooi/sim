package com.black.sim.handler;

import com.black.sim.constant.LoginStatus;
import com.black.sim.other.DefaultUserInfo;
import com.black.sim.other.UserInfo;
import com.black.sim.protobuf.DefaultProtoMsg;
import com.black.sim.server.SessionContext;
import com.black.sim.server.SimServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.HeadType;


/**
 * @description：登录请求处理器
 * @author：8568
 */
@ChannelHandler.Sharable
public class LoginRequestHandler extends AbstractDefaultMsgHandler {

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return HeadType.LOGIN_REQUEST.equals(message.getType());
    }

    @Override
    public void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        // 创建session
        SimServerSession session = new SimServerSession(ctx.channel());
        // 处理登录逻辑
        ctx.executor().submit(() -> login(session, message));
    }

    public boolean login(SimServerSession session, DefaultMessage message) {
        boolean result = false;
        DefaultProtoMsg.ProtoMsg.LoginRequest info = message.getLoginRequest();
        long seqNo = message.getSequence();
        UserInfo u = DefaultUserInfo.fromMsg(info);
        // 构造响应报文
        DefaultMessage msg = DefaultMessage.newBuilder()
                .setType(HeadType.LOGIN_RESPONSE)
                .setSessionId("-1")
                .setSequence(seqNo)
                .build();
        // 用户校验
        LoginStatus statue = checkUser(u);
        DefaultProtoMsg.ProtoMsg.LoginResponse.Builder response = buildLoginResponse(statue);
        // 回复客户端登录结果
        session.writeAndFlush(msg.toBuilder().setLoginResponse(response).build());
        if (LoginStatus.SUCCESS.equals(statue)) {
            session.setUser(u);
            session.bind();
            result = true;
        } else {
            session.close();
        }
        return result;
    }

    private DefaultProtoMsg.ProtoMsg.LoginResponse.Builder buildLoginResponse(LoginStatus status) {
        DefaultProtoMsg.ProtoMsg.LoginResponse.Builder response = DefaultProtoMsg.ProtoMsg.LoginResponse.newBuilder()
                .setCode(status.getCode())
                .setInfo(status.getDesc())
                .setExpose(1);
        return response;
    }

    private LoginStatus checkUser(UserInfo user) {
        if (!user.getPassword().equals(user.getUserName())) {
            return LoginStatus.AUTH_FAILED;
        }
        // token
        //校验用户,比较耗时的操作,需要100 ms以上的时间 TODO
        //方法1：调用远程用户restfull 校验服务
        //方法2：调用数据库接口校验

        return LoginStatus.SUCCESS;

    }
}
