/*
 * Project: terzilla
 *
 * File Created at 2019-08-19
 *
 * Copyright 2012-2015 Greenline.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Greenline Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Greenline.com.
 */
package com.dumas.pedestal.framework.springboot.handler.asywork;

import com.dumas.pedestal.framework.springboot.handler.asywork.inf.AsynWork;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 异步工作池
 *
 * @author andaren
 * @version V1.0
 * @since 2019-08-19 17:44
 */
@Component
public class AsynWorkPool {
    @Resource
    private ExecutorService asynWorkExecutorService;

    public void addWork(AsynWork asynWork) {
        asynWorkExecutorService.execute(asynWork);
    }

    public Future addWorkAndGetFuture(AsynWork asynWork) {
        return asynWorkExecutorService.submit(asynWork);
    }
}
