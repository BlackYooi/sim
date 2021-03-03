package com.black.sim.client;

import com.black.sim.configure.ClientConfig;
import com.black.sim.handler.DefaultClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.sim.utils.NotEmptyUtil.notEmptyOrThrow;

/**
 * @description：
 * @author：8568
 */
@Slf4j
@NoArgsConstructor
public class SimClient extends ImBaseClient {

    @Getter
    @Setter
    private NioEventLoopGroup g = null;

    @Getter
    @Setter
    private Bootstrap b;

    @Setter
    @Getter
    private ChannelInitializer<SocketChannel> channelInitializer = null;

    /**
     * 客户端的配置
     */
    private ClientConfig clientConfigure = ClientConfig.getInstance();

    /**
     * 是否采用的默认的消息协议
    */
    private boolean isDefaultMsg = false;

    /**
     * 创建一个默认消息协议的客户端
    */
    public static SimClient defaultClient () {
        SimClient client = new SimClient(true);
        return client;
    }

    public SimClient(Boolean isDefaultMsg) {
        this.isDefaultMsg = isDefaultMsg;
        try {
            initClient();
        } catch (Exception e) {
            log.error("初始化服务器失败");
        }
    }


    private void initClient() throws Exception {
        b = new Bootstrap();
        g = new NioEventLoopGroup();
        b.group(g);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.remoteAddress(clientConfigure.getServerIp(), clientConfigure.getPort());
        // 通道初始化
        if (!isDefaultMsg) {
            // 如果是用户自定义协议、检查是否传入了该协议的处理方式
            notEmptyOrThrow(channelInitializer);
            b.handler(channelInitializer);
        } else {
            // 默认通讯协议的处理方式
            b.handler(new DefaultClientChannelInitializer());
        }
    }

    @Override
    protected boolean connectToServer() {
        log.info("开始连接到服务器");
        try {
            ChannelFuture connect = b.connect().sync();
            if (connect.isSuccess()) {
                // 连接成功时
                SimClientSession simClientSession = new SimClientSession(connect.channel());
                simClientSession.setConnected(true);
                log.info("连接服务器成功");
                this.setSession(simClientSession);
                return true;
            }
        } catch (Exception e) {
            log.error(String.format("连接到服务器失败、原因【%s】", e.getMessage()));
        }
        return false;
    }

    @Override
    public void sendMsg(DefaultMessage message, ChannelFutureListener f) {
        SimClientSession session = (SimClientSession) getSession();
        Channel channel = session.getChannel();
        ChannelFuture channelFuture = channel.writeAndFlush(message);
        channelFuture.addListener(f);
    }

    @Override
    public boolean sendMsgSync(DefaultMessage message) {
        boolean sendResult = false;
        SimClientSession session = (SimClientSession) getSession();
        Channel channel = session.getChannel();
        if (channel.isActive()) {
            try {
                channel.writeAndFlush(message).sync();
                sendResult = true;
            } catch (InterruptedException e) {
                log.error(String.format("发送消息到服务器出错、原因是【%s】", e.getMessage()));
            }
        } else {
            log.error(String.format("发送消息到服务器出错、原因是【%s】", "通道已经关闭"));
        }
        return sendResult;
    }

    @Override
    public void closeClient() {
        try {
            session.close();
            g.close();
        } catch (Exception e) {
            log.error(String.format("关闭客户端失败；原因【%s】", e.getMessage()));
        }
    }
}
