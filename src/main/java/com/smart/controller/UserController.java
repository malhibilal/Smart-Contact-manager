package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.MyMessagge;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    // processing add contact form


    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute("contact")
                                     @RequestParam("profileImage") MultipartFile file,
                                 @Valid Contact contact,
                                 BindingResult bindingResult, Model model,
                                 Principal principal, HttpSession session) {
        try {
            // which  user is logged in
            String name = principal.getName();
            User user = this.userRepository.getUserByEmail(name);
            // processing and uploading the file(image)
            if(file.isEmpty()){
                // empty
                System.out.println("image is empty");

            }else {
                // upload the file to the folder and update the name to contact
                contact.setImage(file.getOriginalFilename());
                File saveFile=new ClassPathResource("static/img").getFile();
               Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("image is uploaded");
            }

            // bi-direction mapping. this will set user id in the contact table
            contact.setUser(user);
            //user has a list of contacts and add that contact in it
            user.getContact().add(contact);
            // save the user with new contact added
            this.userRepository.save(user);
            System.out.println(contact);
            // sending a successful message
            session.setAttribute("message",new MyMessagge("your contact has added!!","success"));
        }catch (Exception e){
            e.printStackTrace();
            // sending error message
            session.setAttribute("message", new MyMessagge("Error adding contact. please try again","danger"));
        }
        return "normal/add_new_contact";

    }
}
