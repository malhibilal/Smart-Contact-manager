package com.smart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgetController {
    Random random = new Random(10000);
@GetMapping("/forget")
    public String openEmailForm(){

        return "forget_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email){

        System.out.println("email :"+email);

        // generating otp of 4 digit

         int otp=random.nextInt(999999);
        System.out.println(otp);

        // write code to send otp to email

        return "verify_otp";
    }
}

