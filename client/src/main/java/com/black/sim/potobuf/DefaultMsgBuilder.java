package com.black.sim.potobuf;

import com.black.sim.client.SimClientSession;
import com.black.sim.configure.ClientConfig;
import com.black.sim.other.ImSession;
import com.black.sim.other.UserInfo;
import com.black.sim.protobuf.DefaultProtoMsg;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * @author：8568
 */
public class DefaultMsgBuilder {

    /**
     * 用户配置
     */
    private static ClientConfig clientConfigure = ClientConfig.getInstance();

    public static DefaultMessage buildHearBeatMsg(SimClientSession session) {
        DefaultMessage message = buildCommon(-1, HeadType.KEEPALIVE_REQUEST, session);
        MessageHeartBeat.Builder builder = MessageHeartBeat.newBuilder()
                .setSeq(0)
                .setJson("{\"from\":\"client\"}")
                .setUid(session.getUser().getUid());
        return message.toBuilder().setHeartBeat(builder).build();
    }

    public static DefaultMessage ofLoginMsg(ImSession session) {
        DefaultMessage message = buildCommon(-1, HeadType.LOGIN_REQUEST, session);
        UserInfo u = session.getUser();
        LoginRequest black = LoginRequest.newBuilder()
                .setAppVersion(String.valueOf(clientConfigure.getVersionNumber()))
                .setUid(u.getUid())
                .setJson(new Gson().toJson(u))
                .build();
        return message.toBuilder().setLoginRequest(black).build();
    }

    /**
     * 构建基础消息部分
     */
    public static DefaultMessage buildCommon(long seqId, HeadType headType, ImSession session) {
        DefaultMessage.Builder builder = DefaultMessage.newBuilder()
                .setType(headType)
                .setSessionId(session.getSessionId())
                .setSequence(seqId);
        return builder.buildPartial();
    }
}
