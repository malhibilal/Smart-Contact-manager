package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.MyMessagge;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", " Smart contact manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title", " About-Smart contact manager");
        return "about";
    }

    @GetMapping("/signup")
    public String signUp(Model model){
        model.addAttribute("title", "Sign up");
        //when the sign up form is filled all the variables will be filled and we need to map it with the sign up form using thymeleaf
        model.addAttribute("user", new User());
        return "signup";
    }
    // this is for registering user. getting the data which user inserted
    @PostMapping("/do_register")
    // @ModelAttribute is used when we have to get data from the user ,
    public String registerUser(@Valid @ModelAttribute User user,BindingResult bindingResult,
                               @RequestParam(defaultValue = "false")
    boolean agreement, Model model ,HttpSession session) {
        try {
            if (!agreement) {
                System.out.println("you have not agreed to the terms and conditions");
                throw new Exception("you have not agreed to the terms and conditions");
            }
            if(bindingResult.hasErrors()) {
                System.out.println("ERROR"+bindingResult.toString());
                model.addAttribute("user",user);
                return "signup";
            }

            // if the user has accepted the terms
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            model.addAttribute("user", user);
            model.addAttribute("agreement", agreement);
            // to send the user to the database
            // we can use this.userRepository.save(user); both are correct
            User result = userRepository.save(user);
            model.addAttribute("user", result);
            System.out.println("agreement" + agreement);
            System.out.println("user " + result);
            // when all the data has been sent successfully, we will set the user to blank
           model.addAttribute("user", new User());
           // to print the message that it has registered successfully
            session.setAttribute("message",new MyMessagge("Registered Successfully","alert-success"));

            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);

            session.setAttribute("message",new MyMessagge("something went wrong!! "+e.getMessage(),"alert-danger"));
            return "signup";
        }

    }

    // handler for custom login
    @GetMapping ("/signin")
    public String customlogin(Model model){
        model.addAttribute("title","Login Page");
        return "login";
    }


}
