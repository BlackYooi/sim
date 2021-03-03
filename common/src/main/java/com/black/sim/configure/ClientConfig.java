package com.black.sim.configure;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description：客户端的配置文件
 * @author：8568
 */
@Data
@Slf4j
public class ClientConfig extends CommonConfig {

    /**
     * 服务器ip
    */
    private String serverIp;

    /**
     * 禁止直接创建对象
    */
    private ClientConfig() {

    }

    /**
     * 配置的单例
    */
    private static ClientConfig singleConfigure = null;

    public static synchronized ClientConfig getInstance() {
        if (null == singleConfigure) {
            ClientConfig c = new ClientConfig();
            c.init();
            // 初始化完成后释放此对象
            c.properties = null;
            singleConfigure = c;
            return singleConfigure;
        } else {
            return singleConfigure;
        }
    }

    @Override
    protected void initConfig() {
        try {
            // todo ip 格式校验
            serverIp = properties.getProperty("serverIP");
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(500);
        }

    }
}
