package com.abel.quartz.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName: ExecuteJob1
 * @Description: 自定义需要执行的任务1
 * @Version: 1.0
 * @Author: Thomas
 * @Date: 2021/5/1 14:55
 */
@Service
public class ExecuteJob1 {
    private static final Logger log = LoggerFactory.getLogger(ExecuteJob1.class);

    /**
     * @Description: 执行具体的任务逻辑（方法名在quartz定义）
     * @Param: []
     * @return: void
     * @Author: Thomas
     * @Date: 2021/5/1 14:55
     */
    public void execute() {
        log.info("ExecuteJob1定时任务执行了。。。。。" + new Date());

    }
}
