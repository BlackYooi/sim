package com.black.sim.server;

import com.black.sim.protobuf.BaseMsg;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
/**
 * @description：服务器基础类
 * @author：8568
 */
abstract class ImBaseServer {

    /**
     * Description: 启动服务器
     *
     * @param
     * @return: boolean
    */
    public abstract void run() throws Exception;

    public abstract boolean isRunning();
}
