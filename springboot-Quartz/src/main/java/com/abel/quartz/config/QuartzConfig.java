package com.abel.quartz.config;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @ClassName: QuartzConfig
 * @Description: 初始化类Quartz环境初始化
 * @Version: 1.0
 * @Author: Thomas
 * @Date: 2021/4/30 22:49
 */
@Configuration
public class QuartzConfig {
    private static final Logger log = LoggerFactory.getLogger(QuartzConfig.class);

    /**
     * 1.通过name+group获取唯一的jobKey;
     * 2.通过groupname来获取其下的所有jobkey
     */
    private final static String GROUP_NAME = "QuartzJobGroups";

    /**
     * 数据源
     */
    @Autowired
    private DataSource dataSource;

    /**
     * @Description: 创建调度器，可以省略的
     * @Param: []
     * @return: org.quartz.Scheduler
     * @Author: Thomas
     * @Date: 2021/4/30 22:58
     */
    @Bean
    public Scheduler scheduler() throws Exception {
        log.info("执行：scheduler()");

        Scheduler scheduler = schedulerFactoryBean().getScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * @Description: 创建调度器工厂bean对象
     * @Param: []
     * @return: org.springframework.scheduling.quartz.SchedulerFactoryBean
     * @Author: Thomas
     * @Date: 2021/4/30 22:58
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        log.info("执行：schedulerFactoryBean()");

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setSchedulerName("Cluster_Scheduler");
        factory.setDataSource(dataSource);
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        // 设置调度器中的线程池。
        factory.setTaskExecutor(schedulerThreadPool());

        // 设置触发器，可以多个
        Trigger trigger1 = trigger1().getObject();
        Trigger trigger2 = trigger2().getObject();
        Trigger[] triggers = {trigger1, trigger2};
        factory.setTriggers(triggers);

        // 设置quartz的配置信息
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    /**
     * @Description: 创建一个调度器的线程池
     * @Param: []
     * @return: java.util.concurrent.Executor
     * @Author: Thomas
     * @Date: 2021/4/30 22:58
     */
    @Bean
    public Executor schedulerThreadPool() {
        log.info("执行：schedulerThreadPool()");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15);
        executor.setMaxPoolSize(25);
        executor.setQueueCapacity(100);

        return executor;
    }

    /**
     * @Description: 读取quartz.properties配置文件
     * @Param: []
     * @return: java.util.Properties
     * @Author: Thomas
     * @Date: 2021/4/30 22:59
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        log.info("执行：quartzProperties()");

        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("config/quartz.properties"));

        // 在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * @Description: 创建触发器工厂
     * @Param: [jobDetail：任务详情, cronExpression：Cron表达式]
     * @return: org.springframework.scheduling.quartz.CronTriggerFactoryBean
     * @Author: Thomas
     * @Date: 2021/5/1 15:15
     */
    private static CronTriggerFactoryBean createCronTriggerFactory(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        return factoryBean;
    }

    /**
     * @Description: 创建JobDetailFactory
     * @Param: [jobClass, groupName：组名称, targetObject：具体的业务类]
     * @return: org.springframework.scheduling.quartz.JobDetailFactoryBean
     * @Author: Thomas
     * @Date: 2021/5/1 15:19
     */
    private static JobDetailFactoryBean createJobDetailFactory(Class<? extends Job> jobClass,
                                                               String groupName,
                                                               String targetObject) {
        log.info("执行：createJobDetailFactory()");

        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // 是否持久化job内容
        factoryBean.setDurability(true);
        // 设置是否多次请求尝试任务。
        factoryBean.setRequestsRecovery(true);
        factoryBean.setGroup(groupName);

        Map<String, String> map = new HashMap<>(2);
        // 指定具体的业务类
        map.put("targetObject", targetObject);
        // 指定具体的业务类方法
        map.put("targetMethod", "execute");
        factoryBean.setJobDataAsMap(map);

        return factoryBean;
    }

    //=========下面配置自定义的trigger和jobdetail===================

    /**
     * @Description: 创建triggerFactoryBean对象
     * @Param: []
     * @return: org.springframework.scheduling.quartz.CronTriggerFactoryBean
     * @Author: Thomas
     * @Date: 2021/4/30 23:00
     */
    @Bean
    public CronTriggerFactoryBean trigger1() {
        log.info("执行：trigger1()");

        // JobDetail任务详情
        JobDetail jobDetail = createJobDetail1().getObject();
        // Cron表达式
        String cronExpression = "0/2 * * * * ?";

        return createCronTriggerFactory(jobDetail, cronExpression);
    }


    /**
     * @Description: 创建JobDetail对象
     * @Param: []
     * @return: org.springframework.scheduling.quartz.JobDetailFactoryBean
     * @Author: Thomas
     * @Date: 2021/4/30 23:00
     */
    @Bean
    public JobDetailFactoryBean createJobDetail1() {
        log.info("执行：createJobDetail1()");
        return createJobDetailFactory(InvokingJobDetailFactory.class, GROUP_NAME, "executeJob1");
    }

    @Bean
    public CronTriggerFactoryBean trigger2() {
        log.info("执行：trigger2()");

        // JobDetail任务详情
        JobDetail jobDetail = createJobDetail2().getObject();
        // Cron表达式
        String cronExpression = "0/10 * * * * ?";

        return createCronTriggerFactory(jobDetail, cronExpression);
    }

    @Bean
    public JobDetailFactoryBean createJobDetail2() {
        log.info("执行：createJobDetail2()");
        return createJobDetailFactory(InvokingJobDetailFactory.class, GROUP_NAME, "executeJob2");
    }


}