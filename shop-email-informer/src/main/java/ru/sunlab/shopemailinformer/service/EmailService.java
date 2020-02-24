package ru.sunlab.shopemailinformer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //метод для демонстрации письма
    void sendMail(String to, String subject, String text) throws MailException{
        System.out.println("\nFrom: info@nordic.com");
        System.out.println("To: "+to);
        System.out.println("Subject: "+subject);
        System.out.println("Body:");
        System.out.println(text);
    }

    //реальный метод
    void sendRealMail(String to, String subject, String text) throws MailException{
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(to);
        smm.setSubject(subject);
        smm.setText(text);
        mailSender.send(smm);
    }
}
