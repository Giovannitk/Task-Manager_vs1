package com.example.taskmanager.task_manager.service;

import org.springframework.beans.factory.annotation.Autowired;

//Support class to create simple email with text.
import org.springframework.mail.SimpleMailMessage;

//Interface provided by Spring to send email using JavaMail.
import org.springframework.mail.javamail.JavaMailSender;

//For Service annotation.
import org.springframework.stereotype.Service;

//@Service: Mark this class as a service bean that will be handled by the Spring container.
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; //JavaMailSender: It is the object that provides the methods to send emails.

    // to: The recipient’s email address.
    // token: The confirmation token to be included in the message.
    public void sendConfirmationEmail(String to, String token) {
    	/*
    	 * Builds a URL that includes the confirmation token as parameter of 
    	 * the query. This link will be included in the body of the email.*/
        String link = "http://localhost:8081/confirm?token=" + token;
        
        //subject: Text that appears as the subject of the email.
        //body: The content of the email, which includes the confirmation 
        //link and an informative message.
        String subject = "Conferma la tua registrazione";
        String body = "Clicca sul link per confermare la registrazione: " + link + "\nAttenzione: il link scade in 24 ore.";

        //Create an instance of SimpleMailMessage, which is a simple email 
        //(text only, without attachments or HTML).
        SimpleMailMessage message = new SimpleMailMessage();
        
        /*	setTo(to): Set the recipient’s email address.
			setSubject(subject): Sets the email’s subject.
			setText(body): Sets the message body.*/
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
