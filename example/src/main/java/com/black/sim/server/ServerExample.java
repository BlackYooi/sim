package com.black.sim.server;

import org.junit.Test;

/**
 * @description：
 * @author：8568
 */
public class ServerExample {

    SimServer server = SimServer.defaultServer();

    @Test
    public void test() throws Exception {
        server.run();
    }
}
