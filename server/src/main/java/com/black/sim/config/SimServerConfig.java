package com.black.sim.config;

import com.black.sim.configure.ServerConfig;

/**
 * @description：
 * @author：8568
 */
public class SimServerConfig extends ServerConfig {

    private SimServerConfig () {
    }

    private static SimServerConfig serverConfig = null;

    public synchronized static SimServerConfig getInstance() {
        serverConfig = null == serverConfig ? newSerConfig() : serverConfig;
        return serverConfig;
    }

    private static SimServerConfig newSerConfig() {
        SimServerConfig simServerConfig = new SimServerConfig();
        simServerConfig.init();
        simServerConfig.properties = null;
        return simServerConfig;
    }

    @Override
    protected void initConfig() {
    }
}
