package com.dumas.pedestal.framework.springboot.handler.asywork.demo;

import com.dumas.pedestal.framework.springboot.handler.asywork.AsynWorkPool;
import com.dumas.pedestal.framework.springboot.handler.asywork.inf.AsynWork;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 多线程同步任务示例
 * 更多优化参考
 * fork&join
 *
 * @author andaren
 * @version V1.0
 * @since 2020-09-11 16:29
 */
//@Component
@Slf4j
public class MultipartWorkDemo {
    @Resource
    private AsynWorkPool asynWorkPool;

    public void doMultipartWork() {
        int workSize = 20;
        CountDownLatch countDownLatch = new CountDownLatch(workSize);
        log.info("开始产生任务：total=[{}]", workSize);
        for (int i = 0; i < workSize; i++) {
            asynWorkPool.addWork(getWork(countDownLatch, i));
        }
        try {
            // 阻塞直到任务完成
            countDownLatch.await();
            log.info("任务执行完成");
        } catch (Throwable t) {
            t.printStackTrace();
            log.info("任务执行异常");
        }

    }

    private AsynWork getWork(CountDownLatch countDownLatch, int i) {
        return () -> {
            log.info(">>> doing work[workNo={}] in thread[name={}].", i, Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(30);
                log.info("work[workNo={}] done in thread[name={}].", i, Thread.currentThread().getName());
            } catch (Throwable t) {
                t.printStackTrace();
                log.error("work[workNo={}] ocured exception in thread[name={}].", i, Thread.currentThread().getName());
            } finally {
                countDownLatch.countDown();
            }
        };
    }

}
