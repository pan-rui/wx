package com.hy.wx.base;

import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;

/**
 * User: zhangkaitao
 * Date: 14-7-31
 * Time: 下午7:10
 * Version: 1.0
 */
public class AsyncExceptionHandler extends SimpleAsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... args) {
        System.err.println("调用异步任务出错了, message : " + throwable.getMessage());
    }
}
