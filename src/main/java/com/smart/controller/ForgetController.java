package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgetController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
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
        String message =""
                +"<div style='border: 1px solid #e2e2e2; padding: 20px;'>"
                +"<h1>"
                +"OTP is: "
                +"<b>"+otp
                +"</n>"
                +"</h1>"
                +"</div>";

        String to= email;
        boolean flag=this.emailService.sendEmail(subject, message, to);
        if(flag){
            session.setAttribute("sessionOTP",otp);
            session.setAttribute("email",email);// we can save it in the database also
            // as long as the session is valid we can use the otp and the email
            return "verify_otp";
        }else {
            session.setAttribute("message","check your email ID");
            return "forget_email_form";
        }

    }
    // verify otp
    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("otp") int otp, HttpSession session){
    int myotp = (int) session.getAttribute("sessionOTP");
    String email = (String)session.getAttribute("email");

    if (myotp==otp){
        // password change form show
        User user=this.userRepository.getUserByEmail(email);
        if(user== null){
            // send error message
            session.setAttribute("message","No user registered with this email address");
            return "forget_email_form";
        }
        else
        {
            // send change password form
        }

        return "password_change_form";
    } else {
        session.setAttribute("message", "You have entered Wrong OTP");
        return "verify_opt";

    }
    }
    // change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword")String newPassword,HttpSession session){
    // get the current email from session and the user associated with this email
    String email= (String)session.getAttribute("email");
    User user=this.userRepository.getUserByEmail(email);
    user.setPassword(this.passwordEncoder.encode(newPassword));
    this.userRepository.save(user);
    return "redirect:/signin?change=password changed successfully...";

    }
}

