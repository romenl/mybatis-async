package com.gameart.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/***
 *@author JackLei
 *@Date 下午 5:44 2018/8/30
 ***/
public abstract class AbstractAsyncWorker<T> implements Runnable {
    private volatile boolean run = true;
    private ConcurrentLinkedQueue<T> asyncTaskQueue;
    private Logger LOGGER = LoggerFactory.getLogger("BENCHMARK");

    public AbstractAsyncWorker(ConcurrentLinkedQueue<T> queue) {
        this.asyncTaskQueue = queue;
    }

    @Override
    public void run() {
        /**执行任务前的时间*/
        long excuteBeforeTimeMills;
        /**执行任务后的时间*/
        long executeAfterTimeMillis;
        /**记录的时间*/
        long recordTimeMillis = 0L;
        /**一分钟任务数量*/
        int taskCount = 0;
        /**任务执行最长时间*/
        long maxExecuteTimeMillis = 0L;
        /**任务执行最短时间*/
        long minExecuteTimeMillis = Long.MAX_VALUE;
        /**任务总耗时*/
        long totalCostExecuteTimeMillis = 0L;
        long sleep = 0L;
        boolean firstExcute = true;
        boolean isCanSleep = false;

        LOGGER.info("[{}] woker start succ.", getName());

        Thread.currentThread().setName("T#" + getName());
        while (run) {
            excuteBeforeTimeMills = System.currentTimeMillis();
            T poll = asyncTaskQueue.poll();
            if (poll == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("[{}] woker idle. queue size = {}", getName(), asyncTaskQueue.size());
                }
                continue;
            }

            execute(poll);

            taskCount++;
            if (firstExcute) {
                recordTimeMillis = System.currentTimeMillis();
                firstExcute = false;
            }
            executeAfterTimeMillis = System.currentTimeMillis();
            long cost = executeAfterTimeMillis - excuteBeforeTimeMills;
            totalCostExecuteTimeMillis += cost;
            if (cost > maxExecuteTimeMillis) {
                maxExecuteTimeMillis = cost;
            }
            if (cost < minExecuteTimeMillis) {
                minExecuteTimeMillis = cost;
            }

            if (taskCount >= getTPS()) {
                isCanSleep = true;
            }
            //时间累积到一秒 或 每秒执行了指定数量的任务，则进行统计。
            if (System.currentTimeMillis() - recordTimeMillis >= 1000L || isCanSleep) {

                LOGGER.info("[{}] > totalCostExecuteTimeMillis = {},taskCount={} ,maxExecuteTimeMillis = {},minExecuteTimeMillis={},avgCost={},queue size = {}",
                        getName(), totalCostExecuteTimeMillis, taskCount,
                        maxExecuteTimeMillis, minExecuteTimeMillis, totalCostExecuteTimeMillis / Float.valueOf((taskCount)), asyncTaskQueue.size());

                maxExecuteTimeMillis = 0L;
                minExecuteTimeMillis = Long.MAX_VALUE;
                taskCount = 0;
                recordTimeMillis = System.currentTimeMillis();
                sleep = (1000L - totalCostExecuteTimeMillis);
                totalCostExecuteTimeMillis = 0L;
            }

            //执行完指定数量任务后，休息一会儿
            if (isCanSleep) {
                if (sleep > 0L) {
                    try {
                        Thread.sleep(sleep);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("[{}] woker sleep {} ms", getName(), sleep);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isCanSleep = false;
            }

        }
        LOGGER.info("[{}] worker stop", getName());
    }

    public abstract String getName();

    public abstract int getTPS();

    public abstract void execute(T t);

    public void shutdown() {
        run = false;
    }

}