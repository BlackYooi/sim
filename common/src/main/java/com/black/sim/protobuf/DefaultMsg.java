package com.black.sim.protobuf;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * @author：8568
 */
public class DefaultMsg implements BaseMsg {

    private DefaultMessage defaultMessage;

    private DefaultMsg(DefaultMessage defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getSessionId() {
        return null;
    }

    public static Boolean notMsg (Object msg) {
        return null == msg || !(msg instanceof DefaultMessage);
    }

    public static DefaultMsg getInstanceByObject(Object msg) {
        if (notMsg(msg)) {
            return null;
        }
        return new DefaultMsg((DefaultMessage) msg);
    }
}
