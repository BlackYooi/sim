package com.black.sim.utils;

import com.black.sim.protobuf.DefaultMsg;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;

/**
 * @description：消息协议工具
 * @author：8568
 */
@Deprecated
public class DefaultMsgUtil {

    /**
     * @see DefaultMsg#notMsg(java.lang.Object)
    */
    public static Boolean notMsg (Object msg) {
        return null == msg || !(msg instanceof DefaultMessage);
    }
}
