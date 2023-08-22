package com.smart.controller;

import com.smart.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgetController {
    @Autowired
    private EmailService emailService;
    Random random = new Random(10000);
@GetMapping("/forget")
    public String openEmailForm(){

        return "forget_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session){

        System.out.println("email :"+email);

        // generating otp of 4 digit

         int otp=random.nextInt(999999);
        System.out.println(otp);

        // write code to send otp to email
        String subject="OTP from smart contact manager";
        String message ="OTP = "+otp+"";
        String to= email;
        boolean flag=this.emailService.sendEmail(subject, message, to);
        if(flag){
            session.setAttribute("OTP",otp);
            return "verify_otp";
        }else {
            session.setAttribute("message","check your email ID");
            return "forget_email_form";
        }

    }
}

