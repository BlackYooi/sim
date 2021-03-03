package com.black.sim.client;

import com.black.sim.other.DefaultUserInfo;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @description：
 * @author：8568
 */
public class ClientExample {

    SimClient client = SimClient.defaultClient();

    @Test
    public void test() throws InterruptedException {
        // connect to server
        if (client.connectToServer()) {
            DefaultUserInfo userInfo = new DefaultUserInfo();
            userInfo.setUid("black");
            userInfo.setUserName("qq85689049");
            userInfo.setPassword("qq85689049");
            client.login(userInfo);
        }
        // send msg
        TimeUnit.HOURS.sleep(1);
    }
}
