package com.gwx.mail;


import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Properties;

/**
 * @ClassName: MailUtilTest
 * @Description:
 * @Auther: Thomas
 * @Version: 1.0
 * @create 2021/5/30 14:40
 */
@Slf4j
@SpringBootTest
public class MailUtilTest {
    private Properties pro = new Properties();
    private String host = "";
    private String sendMail = "";
    private String sendNickname = "";
    private String receiveMail = "";
    private String receiveNickname = "";
    private String mailSubject = "";
    private String mailContent = "";
    private String authUserName = "";
    private String authPassword = "";
    private Date sentDate = new Date();
    private String port = "";


    @Before
    public void setUp() throws Exception {
        log.info("加载配置文件：mailConfig.properties");
        pro = PropertiesUtil.parsePro("/config/mailConfig.properties");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sendMailUseSmtp() {
        log.info("解析配置文件：mailConfig.properties");
        host = pro.getProperty("mail.smtp.host");
        sendMail = pro.getProperty("mail.smtp.sendMail");
        sendNickname = pro.getProperty("mail.smtp.sendNickname");
        receiveMail = pro.getProperty("mail.smtp.receiveMail");
        receiveNickname = pro.getProperty("mail.smtp.receiveNickname");
        mailSubject = pro.getProperty("mail.smtp.mailSubject");
        authUserName = pro.getProperty("mail.smtp.authUserName");
        authPassword = pro.getProperty("mail.smtp.authPassword");
        log.info("文件解析结束");

        mailContent = "sendMailUseSmtp--测试邮件内容";
        try {
            MailUtil.sendMailUseSmtp(host, sendMail, sendNickname, receiveMail, receiveNickname,
                    mailSubject, mailContent, sentDate, authUserName, authPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendMailUsePOP3() {
        log.info("解析配置文件：mailConfig.properties");
        host = pro.getProperty("mail.POP3.host");
        sendMail = pro.getProperty("mail.POP3.sendMail");
        sendNickname = pro.getProperty("mail.POP3.sendNickname");
        receiveMail = pro.getProperty("mail.POP3.receiveMail");
        receiveNickname = pro.getProperty("mail.POP3.receiveNickname");
        mailSubject = pro.getProperty("mail.POP3.mailSubject");
        authUserName = pro.getProperty("mail.POP3.authUserName");
        authPassword = pro.getProperty("mail.POP3.authPassword");
        port = pro.getProperty("mail.POP3.port");
        log.info("文件解析结束");

        mailContent = "sendMailUsePOP3--测试邮件内容";
        try {
            MailUtil.sendMailUsePOP3(host, sendMail, sendNickname, receiveMail, receiveNickname,
                    mailSubject, mailContent, sentDate, authUserName, authPassword, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendMail2() {
    }

    @Test
    public void createMimeMessage() {
    }

    @Test
    public void createComplexMimeMessage() {
    }

    @Test
    public void getMailContentImage() {
    }

    @Test
    public void getMailContentText() {
    }

    @Test
    public void getMailContentAttachment() {
    }
}