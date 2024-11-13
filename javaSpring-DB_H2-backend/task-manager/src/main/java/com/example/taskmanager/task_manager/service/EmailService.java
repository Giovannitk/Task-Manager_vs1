package com.example.taskmanager.task_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String token) {
        String link = "http://localhost:8081/confirm?token= " + token;
        String subject = "Conferma la tua registrazione";
        String body = "Clicca sul link per confermare la registrazione: " + link + "\nAttenzione: il link scade in 24 ore.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
