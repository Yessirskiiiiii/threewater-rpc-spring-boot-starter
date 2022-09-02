package com.threewater.rpc.client.net;

import com.sun.istack.internal.NotNull;

import java.util.concurrent.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/01/16:58
 * @Description: RpcFuture 接收响应结果
 */
public class RpcFuture<T> implements Future<T> {

    private T response;

    /**
     * 倒计时器
     */
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * Future 的请求时间，用于计算 Future 是否超时
     */
    private final long beginTime = System.currentTimeMillis();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    /**
     * 获取响应，直到有结果才返回
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (countDownLatch.await(timeout, unit)) {
            return response;
        }
        return null;
    }

    public void setResponse(T response) {
        this.response = response;
        countDownLatch.countDown();
    }

    public long getBeginTime() {
        return beginTime;
    }

}
