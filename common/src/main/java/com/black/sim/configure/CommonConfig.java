package com.black.sim.configure;

import com.black.sim.utils.IOUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class CommonConfig {
    /**
     * 配置文件名
     */
    private static final String CONFIG_NAME = "Sim.properties";

    /**
     * 解析配置文件的对象
     */
    protected Properties properties = null;

    /**
     * 魔数
    */
    @Getter
    protected short magicCode = 0;

    /**
     * 版本数字编码
    */
    @Getter
    protected short versionNumber = 0;

    /**
     * 心跳时间间隔、单位-秒
    */
    @Getter
    protected Integer heartBeatInterval = 60 * 60;

    /**
     * 服务器端口
     */
    @Getter
    protected Integer port;

    /**
     * 加载配置文件
     */
    protected void loadPropertiesFromFile() throws Exception{
        try {
            properties = IOUtil.getProperties(CONFIG_NAME);
        } catch (Exception e) {
            throw new Exception(String.format("读取配置文件错误、原因【%s】", e.getMessage()));
        }
    }

    private static CommonConfig simSingleCommonConfig = null;

    public static CommonConfig getInstance() {
        if (null == simSingleCommonConfig) {
            CommonConfig c = new CommonConfig();
            c.init();
            c.properties = null;
            simSingleCommonConfig = c;
            return c;
        } else {
            return simSingleCommonConfig;
        }
    }

    protected void init() {
        try {
            loadPropertiesFromFile();
            magicCode = Short.valueOf(properties.getProperty("magicCode"));
            versionNumber = Short.valueOf(properties.getProperty("versionNumber"));
            heartBeatInterval = Integer.valueOf(properties.getProperty("heartBeatInterval"));
            // todo port 范围校验
            port = Integer.valueOf(properties.getProperty("port"));
            // 初始化子类配置
            initConfig();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(500);
        }
    }

    /**
     * 初始化子类自定义配置
    */
    protected void initConfig(){};
}
