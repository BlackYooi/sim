package com.black.sim.exception;

/**
 * @description：
 * @author：8568
 */
public class ServerCanNotAvailableException extends Exception{
    public ServerCanNotAvailableException() {
        super("服务器不可达");
    }
}
