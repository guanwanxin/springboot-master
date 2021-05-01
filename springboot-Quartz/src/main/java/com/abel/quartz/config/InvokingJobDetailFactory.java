package com.abel.quartz.config;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;


/**
 * @ClassName: InvokingJobDetailFactory
 * @Description: 任务同一调度（反射原理）
 * @Version: 1.0
 * @Author: Thomas
 * @Date: 2021/5/1 14:52
 */
public class InvokingJobDetailFactory extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(InvokingJobDetailFactory.class);

    /**
     * 计划任务所在类
     */
    private String targetObject;

    /**
     * 具体需要执行的计划任务
     */
    private String targetMethod;

    private ApplicationContext ctx;

    /**
     * @Description: 反射原理调度需要执行的类、方法
     * @Param: [context]
     * @return: void
     * @Author: Thomas
     * @Date: 2021/5/1 14:54
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("执行executeInternal(JobExecutionContext context)");
        try {
            Object obj = ctx.getBean(targetObject);
            Method method = null;
            try {
                method = obj.getClass().getMethod(targetMethod);
                log.info("执行method：" + method);
                //调用被代理对象的方法
                method.invoke(obj);
                log.info("obj：" + obj);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }
}
