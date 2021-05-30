package com.gwx.mail.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @ClassName: EmailAction
 * @Description: 邮件发送
 * @Version: 1.0
 * @Author: Thomas
 * @Date: 2021/5/2 11:06
 */
@RestController
@RequestMapping(value = "email")
public class EmailController {
    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private final JavaMailSender mailSender;

    /**
     * 发送者账号
     */
    @Value("${spring.mail.username}")
    private String sendName;

    /**
     * 邮件模板引擎
     */
    @Autowired(required=true)
    private TemplateEngine templateEngine;

    public EmailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * @Description: 发送简单的文本邮件
     * @Param: [msg, email]
     * @return: java.lang.String
     * @Author: Thomas
     * @Date: 2021/5/2 11:07
     */
    @PostMapping(value = "simple")
    public String sendSimpleMsg(String msg, String email) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(sendName);
            mail.setTo(email);
            mail.setSubject("这是一封简单邮件");
            mail.setText(msg);
            mailSender.send(mail);
            return "发送成功";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "发送失败:" + ex.getMessage();
        }
    }

    /**
     * @Description: 发送HTML邮件
     * @Param: [msg, email]
     * @return: java.lang.String
     * @Author: Thomas
     * @Date: 2021/5/2 11:07
     */
    @PostMapping(value = "html")
    public String sendHtmlMsg(String msg, String email) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(sendName);
            messageHelper.setTo(email);
            messageHelper.setSubject("HTML邮件");
            String html = "<div><h1><a name=\"hello\"></a><span>Hello</span></h1><blockquote><p><span>this is a html email.</span></p></blockquote><p>&nbsp;</p><p><span>"
                    + msg + "</span></p></div>";
            String aa = "https://www.baidu.com/";
            String html2 = "<a href='" + aa + "'>" + aa + "</a>";
            messageHelper.setText(html2, true);
            mailSender.send(message);
            return "发送成功";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "发送失败：" + e.getMessage();
        }
    }

    /**
     * @Description: 发送包含附件的邮件
     * @Param: [msg, email]
     * @return: java.lang.String
     * @Author: Thomas
     * @Date: 2021/5/2 11:08
     */
    @PostMapping(value = "mime_with_file")
    public String sendWithFile(String msg, String email) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(sendName);
            messageHelper.setTo(email);
            messageHelper.setSubject("一封包含附件的邮件");
            messageHelper.setText(msg);
            // 该文件位于resources目录下
            // 文件路径不能直接写文件名，系统会报错找不到路径，而IDEA却能直接映射过去
            // 文件路径可以写成相对路径src/main/resources/x.pdf，也可以用绝对路径：System.getProperty("user.dir") + "/src/main/resources/x.pdf"
            File file = new File("src/main/resources/权限关系.txt");
            //File file = new File(System.getProperty("user.dir") + "/src/main/resources/SpringBoot日志处理之Logback.pdf");
            System.out.println("文件是否存在：" + file.exists());
            messageHelper.addAttachment(file.getName(), file);
            mailSender.send(message);
            return "发送成功";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "发送失败：" + e.getMessage();
        }
    }

    /**
     * @Description: 发送带静态资源图片的HTML邮件
     * @Param: [msg, email]
     * @return: java.lang.String
     * @Author: Thomas
     * @Date: 2021/5/2 11:08
     */
    @PostMapping(value = "html_with_img")
    public String sendHtmlWithImg(String msg, String email) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(sendName);
            messageHelper.setTo(email);
            messageHelper.setSubject("带静态资源图片的HTML邮件");
            String html = "<div><h1><a name=\"hello\"></a><span>Hello</span></h1><blockquote><p><span>this is a html email.</span></p></blockquote><p>&nbsp;</p><p><span>"
                    + msg + "</span></p><img src='cid:myImg' /></div>";
            messageHelper.setText(html, true);
            File file = new File("src/main/resources/timg.jpg");
            messageHelper.addInline("myImg", file);
            mailSender.send(message);
            return "发送成功";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "发送失败：" + e.getMessage();
        }
    }

    /**
     * @Description: 使用HTML模板文件发送邮件
     * @Param: [msg, email]
     * @return: java.lang.String
     * @Author: Thomas
     * @Date: 2021/5/2 11:08
     */
    @PostMapping(value = "html_with_template")
    public String sendHtmlByTemplate(String msg, String email) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(sendName);
            messageHelper.setTo(email);
            messageHelper.setSubject("使用HTML模板文件发送邮件");

            Context context = new Context();
            context.setVariable("msg", msg);
            messageHelper.setText(templateEngine.process("EmailTemplate", context), true);
            mailSender.send(message);
            return "发送成功";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "发送失败：" + e.getMessage();
        }
    }
}