package com.black.sim.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @description：
 * @author：8568
 */
public class IOUtil {

    /**
     * 读取配置文件
    */
    public static Properties getProperties(String name) throws IOException {
        Properties properties = new Properties();
        try (InputStream resourceAsStream = IOUtil.class.getClassLoader().getResourceAsStream(name)) {
            properties.load(resourceAsStream);
        } catch (Exception e) {
            throw e;
        }
        return properties;
    }
}
