package com.gwx.mail.utils;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

/**
 * @ClassName: MailUtil
 * @Description: 邮件处理
 * @Version: 1.0
 * @Author: Thomas
 * @Date: 2021/5/17 11:21
 */
public class MailUtil {
    /**
     * @Description: 发送一封邮件的过程
     * @Param: [
     * smtpHost     smtp服务，一般为smtp.163.com  smtp.qq.com
     * sendMail     发件人邮箱地址 一般为smtp对应
     * receiveMail  接收人邮箱地址，可以是任意的合法邮箱即可
     * mailSubject  创建的邮件主题
     * mailContent  创建邮件的内容，可以添加html标签
     * sentDate     设置发送时间，null为立即发送
     * authUserName 验证服务器是的用户名，一般和发件人邮箱保持一致
     * authPassword 验证服务器的密码，一般为登录邮箱的密码，也可能是邮箱独立密码
     * ]
     * @return: void
     * @Author: Thomas
     * @Date: 2021/5/29 17:11
     */
    public static void sendMailUseSmtp(String smtpHost, String sendMail, String sendNickname,
                                String receiveMail, String receiveNickname,
                                String mailSubject, String mailContent,
                                Date sentDate, String authUserName,
                                String authPassword) throws Exception {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        // 参数配置
        Properties props = new Properties();
        // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.transport.protocol", "smtp");
        // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.host", smtpHost);
        // 需要请求认证
        props.setProperty("mail.smtp.auth", "true");

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        // 设置为debug模式, 可以查看详细的发送 log,开发时候使用
        session.setDebug(true);

        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, sendMail, sendNickname,
                receiveMail, receiveNickname, mailSubject, mailContent, sentDate);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        transport.connect(smtpHost, authUserName, authPassword);

        // 6. 发送邮件, 发到所有的收件地址,
        // message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();
    }

    /**
     * @Description: 发送一封邮件的过程（POP3）
     * @Param: [
     * smtpHost     smtp服务，一般为smtp.163.com  smtp.qq.com
     * sendMail     发件人邮箱地址 一般为smtp对应
     * receiveMail  接收人邮箱地址，可以是任意的合法邮箱即可
     * mailSubject  创建的邮件主题
     * mailContent  创建邮件的内容，可以添加html标签
     * sentDate     设置发送时间，null为立即发送
     * authUserName 验证服务器是的用户名，一般和发件人邮箱保持一致
     * authPassword 验证服务器的密码，一般为登录邮箱的密码，也可能是邮箱独立密码
     * port         端口
     * ]
     * @return: void
     * @Author: Thomas
     * @Date: 2021/5/29 17:11
     */
    public static void sendMailUsePOP3(String smtpHost, String sendMail, String sendNickname,
                                String receiveMail, String receiveNickname,
                                String mailSubject, String mailContent,
                                Date sentDate, String authUserName,
                                String authPassword, String port) throws Exception {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        // 参数配置
        Properties props = new Properties();
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", port);
        props.setProperty("mail.pop3.socketFactory.port", port);
        // 发件人的邮箱的 SMTP 服务器地址
        props.put("mail.pop3.host", smtpHost);

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        // 设置为debug模式, 可以查看详细的发送 log,开发时候使用
        session.setDebug(true);

        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, sendMail, sendNickname,
                receiveMail, receiveNickname, mailSubject, mailContent, sentDate);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        transport.connect(smtpHost, authUserName, authPassword);

        // 6. 发送邮件, 发到所有的收件地址,
        // message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();
    }

    /**
     * @Description: 发送一封邮件的过程
     * @Param: [
     * smtpHost        smtp服务，一般为smtp.163.com  smtp.qq.com
     * sendMail        发件人邮箱地址 一般为smtp对应
     * sendNickname    发件人昵称
     * receiveMail     接收人邮箱地址，可以是任意的合法邮箱即可
     * receiveNickname 接收人昵称
     * mailSubject     创建的邮件主题
     * mimeMultipart   上传一个复杂的邮件内容
     * sentDate        设置发送时间，null为立即发送
     * authUserName    验证服务器是的用户名，一般和发件人邮箱保持一致
     * authPassword    验证服务器的密码，一般为登录邮箱的密码，也可能是邮箱独立密码
     * ]
     * @return: void
     * @Author: Thomas
     * @Date: 2021/5/29 17:15
     */
    public static void sendMail(String smtpHost, String sendMail,
                                String sendNickname, String receiveMail,
                                String receiveNickname, String mailSubject,
                                MimeMultipart mimeMultipart, Date sentDate,
                                String authUserName, String authPassword) throws Exception {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();
        // 参数配置
        // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.transport.protocol", "smtp");
        // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.host", smtpHost);
        // 需要请求认证
        props.setProperty("mail.smtp.auth", "true");

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true); // 设置为debug模式, 可以查看详细的发送 log,开发时候使用

        // 3. 创建一封邮件
        MimeMessage message = createComplexMimeMessage(session, sendMail, sendNickname,
                receiveMail, receiveNickname, mailSubject, mimeMultipart, sentDate);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        transport.connect(smtpHost, authUserName, authPassword);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();
    }

    /**
     * @Description: 创建一封只包含文本的简单邮件
     * * @Param: [
     * session         和服务器交互的会话
     * sendMail        发件人邮箱
     * sendNickname    发送人昵称
     * receiveMail     收件人邮箱
     * receiveNickname 收件人昵称
     * mailSubject     邮件主题
     * receiveMail     邮件内容
     * sentDate        设置发送时间，null为立即发送
     * ]
     * @return: javax.mail.internet.MimeMessage
     * @Author: Thomas
     * @Date: 2021/5/29 17:33
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail,
                                                String sendNickname, String receiveMail,
                                                String receiveNickname, String mailSubject,
                                                String mailContent, Date sentDate) throws Exception {
        String encoding = "UTF-8";
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, sendNickname, encoding));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, receiveNickname, encoding));
        // 4. Subject: 邮件主题
        message.setSubject(mailSubject, "UTF-8");
        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(mailContent, "text/html;charset=UTF-8");
        // 6. 设置发件时间
        message.setSentDate(sentDate != null ? sentDate : new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;
    }

    /**
     * @Description: 创建一封复杂邮件（文本+图片+附件）
     * @Param: [
     * session          和服务器交互的会话
     * sendMail         发件人邮箱
     * sendNickname     发送人昵称
     * receiveMail      收件人邮箱
     * receiveNickname  收件人昵称
     * mailSubject      邮件主题
     * mailContent      邮件内容
     * sentDate        设置发送时间，null为立即发送
     * ]
     * @return: javax.mail.internet.MimeMessage
     * @Author: Thomas
     * @Date: 2021/5/29 17:34
     */
    public static MimeMessage createComplexMimeMessage(Session session, String sendMail,
                                                       String sendNickname, String receiveMail,
                                                       String receiveNickname, String mailSubject,
                                                       MimeMultipart mailContent, Date sentDate) throws Exception {
        // 1. 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, sendNickname, "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiveMail, receiveNickname, "UTF-8"));
        // 4. Subject: 邮件主题
        message.setSubject(mailSubject, "UTF-8");
        // 5. Content: 复杂邮件内容，有单独方法创建，直接传入
        // 6. 设置整个邮件的关系（将最终的混合“节点”作为邮件的内容添加到邮件对象）
        message.setContent(mailContent);
        // 7. 设置发件时间
        message.setSentDate(sentDate != null ? sentDate : new Date());
        // 8. 保存上面的所有设置
        message.saveChanges();
        return message;
    }

    /**
     * @Description: 创建一个图片节点，返回节点唯一编号，在文本中引用
     * @Param: [
     * imagePath 需要添加到邮件正文文本中的图片本地路径,
     * only_image_ID 设置的图片Id，只要是正文文本中引用图片只需引用该ID即可
     * ]
     * @return: com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart 返回创建的图片节点
     * @Author: Thomas
     * @Date: 2021/5/29 23:15
     */
    public static MimeBodyPart getMailContentImage(String imagePath, String only_image_ID) {
        MimeBodyPart image = new MimeBodyPart();
        // 读取本地文件
        DataHandler dh = new DataHandler(new FileDataSource(imagePath));
        // 将图片数据添加到“节点”
        image.setDataHandler(dh);
        // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）
        image.setContentID(only_image_ID);
        return image;
    }

    /**
     * @Description: 创建一个文本节点，其中添加创建的图片节点对图片进行引用
     * @Param: [content 本，可以是已经饮用过的文本，例如"这是一张图片<br/><img src='cid:only_image_ID'/>"]
     * @return: com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart
     * @Author: Thomas
     * @Date: 2021/5/29 23:15
     */
    public static MimeBodyPart getMailContentText(String content) {
        MimeBodyPart text = new MimeBodyPart();
        //这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
        //text.setContent("这是一张图片<br/><img src='cid:image_fairy_tail'/>", "text/html;charset=UTF-8");
        text.setContent(content, "text/html;charset=UTF-8");
        return text;
    }

    /**
     * @Description: 创建一个附件节点，附件上传文件
     * @Param: [attachmentPath 需要上传到附件的文件的路径]
     * @return: com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart 返回一个节点
     * @Author: Thomas
     * @Date: 2021/5/29 23:14
     */
    public static MimeBodyPart getMailContentAttachment(String attachmentPath)
            throws UnsupportedEncodingException,
            com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException {
        MimeBodyPart attachment = new MimeBodyPart();
        // 读取本地文件
        DataHandler dh = new DataHandler(new FileDataSource(attachmentPath));
        // 将附件数据添加到“节点”
        attachment.setDataHandler(dh);
        // 设置附件的文件名（需要编码）
        attachment.setFileName(MimeUtility.encodeText(dh.getName()));
        return attachment;
    }


    /**
     * 创建一个混合节点，添加所有的普通节点
     *
     * @param mimeBodyPart 传入需要添加的普通节点数组
     * @return MimeMultipart 混合节点
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
//    public static MimeMultipart getMailContentMultipart(MimeBodyPart[] mimeBodyPart) {
//        MimeMultipart mimeMultipart = new MimeMultipart();
//        for (int i = 0; i < mimeBodyPart.length; i++) {
//            mimeMultipart.addBodyPart(mimeBodyPart[i]);
//        }
//        return mimeMultipart;
//    }
}

