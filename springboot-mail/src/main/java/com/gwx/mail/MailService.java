package com.gwx.mail;

/**
 * @ClassName: MailService
 * @Description:
 * @Auther: Thomas
 * @Version: 1.0
 * @create 2021/5/1 22:26
 */
public interface MailService {

    void sendSimpleMail(String to, String subject, String content);

    void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId);
}
