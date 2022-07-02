package com.stl.bsky.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.stl.bsky.entity.uac.UserMaster;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class EmailSender {

    @Value("${registration.siteURL}")
    private String siteURL;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Autowired
    private JavaMailSender javaMailSender;


    public void sendSimpleMessage(String to, String sub, String text) {
        log.info("Inside Mail Sender");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(sub);
        message.setText(text);
        try {
            javaMailSender.send(message);
            log.info("Mail Sent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendVerificationEmail(UserMaster user)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmailId();
        String senderName = "Team STL";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Team STL";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName());
        String verifyURL = siteURL + "/" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        javaMailSender.send(message);

    }
}
