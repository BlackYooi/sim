package com.black.sim.exception;

/**
 * @description：
 * @author：8568
 */
public class NotLoginException extends Exception{

    public NotLoginException() {
        super("用户未登录");
    }
}
