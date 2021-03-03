package com.black.sim.server;

import com.black.sim.config.SimServerConfig;
import com.black.sim.handler.DefaultServerChannelInitializer;
import com.black.sim.protobuf.BaseMsg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Setter;

import java.net.InetSocketAddress;

import static com.black.sim.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.sim.utils.NotEmptyUtil.notEmptyOrThrow;
/**
 * @description：
 * @author：8568
 */
public class SimServer extends ImBaseServer{

    /**
     * 是否是默认的通讯协议
    */
    private boolean isDefaultMsg = false;

    /**
     * 通讯协议的类型
    */
    private static Class msgType;

    /**
     * 该服务器的配置
    */
    private static SimServerConfig config = SimServerConfig.getInstance();

    /**
     * 自定义流水线
    */
    @Setter
    private ChannelInitializer<SocketChannel> channelInitializer;

    /**
     * 服务器是否启动成功
    */
    private boolean isRunSuccess = false;

    private SimServer(boolean isDefaultMsg) {
        this.isDefaultMsg = isDefaultMsg;
    }

    /**
     * 一台机器只能开一个服务器、所以设置成单例。
     * 多开了反而会影响性能：
     * 服务能力相同、线程切换的开销更大
    */
    private static SimServer server = null;

    public synchronized static SimServer defaultServer() {
        if (null == server) {
            SimServer s = new SimServer(true);
            server = s;
            return s;
        }
        return server;
    }

    @Override
    public void run() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        try (NioEventLoopGroup boss = new NioEventLoopGroup(1);
            NioEventLoopGroup worker = new NioEventLoopGroup();
        ) {
            // 初始化bootstrap
            initBootStrap(b, boss, worker);
            // 装配流水线
            if (!isDefaultMsg) {
                notEmptyOrThrow(channelInitializer);
                b.childHandler(channelInitializer);
            } else {
                b.childHandler(new DefaultServerChannelInitializer());
            }
            // 等待绑定成功
            ChannelFuture bindF = b.bind().sync();
            if (bindF.isSuccess() ) {
                isRunSuccess = true;
            }
            // 一直阻塞、直到发出关闭命令、本服务器不考虑关闭、所以会一直阻塞
            ChannelFuture channelFuture = bindF.channel().closeFuture();
            channelFuture.sync();  
        }
    }

    private void initBootStrap(ServerBootstrap b, NioEventLoopGroup boss, NioEventLoopGroup worker) {
        //1 设置reactor 线程
        b.group(boss, worker);
        //2 设置nio类型的channel
        b.channel(NioServerSocketChannel.class);
        //3 设置监听端口
        b.localAddress(new InetSocketAddress(config.getPort()));
        //4 设置通道选项
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR,
                PooledByteBufAllocator.DEFAULT);
    }

    @Override
    public boolean isRunning() {
        return isRunSuccess;
    }
}
