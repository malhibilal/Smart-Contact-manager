package com.smart.service;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.InternetAddress;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

@Service
public class EmailService {
    public boolean sendEmail(String subject, String message, String to){

        // method was void, but now its boolean, create a variable and set it to false
        boolean f= false;
        String from ="bejaz2800@gmail.com";

        // variable for gmail host: smtp.gmail.com.
        String host= "smtp.gmail.com";
        // get the system properities System.getProperties();
        Properties properties=System.getProperties();
        System.out.println("PROPERTIES " + properties);
        // setting important information to properities object for host
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");
        // step 1: to get the session object to send the email. without session we cant send the email
        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("bejaz2800@gmail.com","djoztjkrlqrikuxw");
            }
        });
        session.setDebug(true); // to debug the errors

        // step 2: compose the message [text, photo, multimedia, pdf. ] mineMessage
        MimeMessage mimeMessage= new MimeMessage(session);
        // we have to set properities. from where we are sending the messange
        // from email id
        try {

            mimeMessage.setFrom(from);
            //adding recepient
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // adding subject to message
            mimeMessage.setSubject(subject);
            // adding text to message
            mimeMessage.setContent(message,"text/html");

            // STEP 3: Send the message using transport class

            Transport.send(mimeMessage);
            System.out.println("Sent sucessfully");
            f=true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return f;
    }


}
