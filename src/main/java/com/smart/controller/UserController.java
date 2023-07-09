package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        // getting the user by principal. in this method we are getting the user
        // who is logged in and sending the data to the model.
        String email= principal.getName();
        User user=userRepository.getUserByEmail(email);
        System.out.println("user: "+user);
        model.addAttribute("user",user);
    }
    // dash board home controller
    @RequestMapping("/index")
    public String dashBord(Model model, Principal principal){
        model.addAttribute("title","User Dashborard");
        return "normal/user_dashbord";
    }
    @GetMapping("/add-contact")
    public String addNewContact(Model model){
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact", new Contact());

        return "normal/add_new_contact";

    }
}
