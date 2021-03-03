package com.black.sim.server;

import com.black.sim.other.UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author 85689
 */
@Slf4j
public final class SessionContext {

    private SessionContext() {
    }

    private static SessionContext singleInstance = new SessionContext();

    /**
     * 会话集合
    */
    private ConcurrentHashMap<String, SimServerSession> map = new ConcurrentHashMap<String, SimServerSession>();

    public static SessionContext getInstance() {
        return singleInstance;
    }

    /**
     * 增加session对象
     */
    public void addSession(
            String sessionId, SimServerSession s) {
        map.put(sessionId, s);
        log.info("用户登录:id= " + s.getUser().getUid()
                + "   在线总数: " + map.size());

    }

    /**
     * 获取session对象
     */
    public SimServerSession getSession(String sessionId) {
        if (map.containsKey(sessionId)) {
            return map.get(sessionId);
        } else {
            return null;
        }
    }

    /**
     * 根据用户id，获取session对象
     */
    public List<SimServerSession> getSessionsBy(String userId) {

        List<SimServerSession> list = map.values()
                .stream()
                .filter(s -> s.getUser().getUid().equals(userId))
                .collect(Collectors.toList());
        return list;
    }

    /**
     * 删除session
     */
    public void removeSession(String sessionId) {
        if (!map.containsKey(sessionId)) {
            return;
        }
        SimServerSession s = map.get(sessionId);
        map.remove(sessionId);
        log.info("用户下线:id= " + s.getUser().getUid()
                + "   在线总数: " + map.size());
    }


    public boolean hasLogin(UserInfo user) {
        Iterator<Map.Entry<String, SimServerSession>> it =
                map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, SimServerSession> next = it.next();
            UserInfo u = next.getValue().getUser();
            if (u.getUid().equals(user.getUid())) {
                return true;
            }
        }
        return false;
    }
}
