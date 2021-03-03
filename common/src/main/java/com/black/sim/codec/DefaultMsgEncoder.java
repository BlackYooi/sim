package com.black.sim.codec;

import com.black.sim.configure.CommonConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
/**
 * @description：编码器
 * @author：8568
 */
public class DefaultMsgEncoder extends MessageToByteEncoder<DefaultMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, DefaultMessage msg, ByteBuf out) throws Exception {
        CommonConfig commonConfig = CommonConfig.getInstance();
        // 魔数
        out.writeShort(commonConfig.getMagicCode());
        // 版本号
        out.writeShort(commonConfig.getVersionNumber());
        byte[] bytes = msg.toByteArray();
        int msgLength = bytes.length;
        // 内容长度
        out.writeInt(msgLength);
        // 内容
        out.writeBytes(bytes);
    }
}
