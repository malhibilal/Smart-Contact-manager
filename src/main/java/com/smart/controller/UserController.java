package com.smart.controller;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.MyMessagge;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        // getting the user by principal. in this method we are getting the user
        // who is logged in and sending the data to the model.
        String email = principal.getName();
        User user = userRepository.getUserByEmail(email);
        System.out.println("user: " + user);
        model.addAttribute("user", user);
    }

    // dash board home controller
    @RequestMapping("/index")
    public String dashBord(Model model, Principal principal) {
        model.addAttribute("title", "User Dashborard");
        return "normal/user_dashbord";
    }

    @GetMapping("/add-contact")
    public String addNewContact(Model model) {
        model.addAttribute("title", "Add Contact");
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
            if (file.isEmpty()) {
                // empty
                System.out.println("image is empty");
                // if the user has not selected an image, then default image will appear
                contact.setImage("contact.png");


            } else {
                // upload the file to the folder and update the name to contact
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
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
            session.setAttribute("message", new MyMessagge("your contact has added!!", "success"));
        } catch (Exception e) {
            e.printStackTrace();
            // sending error message
            session.setAttribute("message", new MyMessagge("Error adding contact. please try again", "danger"));
        }
        return "normal/add_new_contact";

    }

    // show all the contacts
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

        model.addAttribute("title", "Show Contacts");
        // one way
        //String name=principal.getName();
        //User user =userRepository.getUserByEmail(name);
        // List<Contact> contact =user.getContact(); //send the list to html page via thymeleaf

        // second way - through contact repository
        // use principal to get the name of the user,username gets the whole user from the repository
        String userName = principal.getName();
        User user = this.userRepository.getUserByEmail(userName);

        // to set the two variables, page and the number of contact per page
        Pageable pageable = PageRequest.of(page, 5);
        //List<Contact> contacts=this.contactRepository.findContactsByUser(user.getId()); without pagination
        // added pageable object and changed from list to page
        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

        model.addAttribute("contacts", contacts); // list of contacts
        model.addAttribute("currentPage", page); // current page
        model.addAttribute("totalPages", contacts.getTotalPages());// total pages

        return "normal/show_contacts";
    }

    // getting a specific contact details
    @GetMapping("/{cId}/contact") // user is already at the top mapped.
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();
        // now to check which user is logged in
        String userName = principal.getName();
        User user = this.userRepository.getUserByEmail(userName);
        // now we have the user, we will use if cond. to chck
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());

        }
        return "normal/contact_detail";
    }

    //delete contact handler

    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Model model,
                                Principal principal,HttpSession session) {

       /* // get the contact details to be deleted
        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();
        // another easier way for the above two lines of getting the contact
        //Contact contact =this.contactRepository.findById(cId).get();
        // check which user is logged in
        String name = principal.getName();
        User user = this.userRepository.getUserByEmail(name);
        if (user.getId() == contact.getUser().getId()) {
            contact.setUser(null);
            this.contactRepository.delete(contact);
        }*/
        // new way of doing the delete function. get the contact, get the user and list of all the contacts
        // and then remove a particular contact and then save the usr

        Contact contact=this.contactRepository.findById(cId).get();
        User user=this.userRepository.getUserByEmail(principal.getName());
        user.getContact().remove(contact);
        this.userRepository.save(user);
        session.setAttribute("message",new MyMessagge("Contact deleted successfully !!","success"));
        return "redirect:/user/show-contacts/0";
        // /user/show-contacts/0 is the url for showing the contact and 0 means the page on the
        // 0th index i.e. page 1

    }

    // open update contact handler
    @PostMapping("/update-contact/{cId}")
    public String updateCOontact(@PathVariable("cId") Integer cId, Model model){
        model.addAttribute("title","Update Contact");
        Contact contact=this.contactRepository.findById(cId).get();
        model.addAttribute("contact",contact);

        return "normal/update_form";
    }

    // update contact handler
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage")
    MultipartFile file, Model model,HttpSession session, Principal principal
    ){
        try {
            // old contact details
            Contact oldContact=this.contactRepository.findById(contact.getcId()).get();
            // image
            if(!file.isEmpty()){
                // rewirte the new file
                // delete old photo
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile,oldContact.getImage());
                file1.delete();

                // update new photo

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

            } else {
                contact.setImage(file.getOriginalFilename());
            }
            User user= this.userRepository.getUserByEmail(principal.getName());
            contact.setUser(user); // user and contact are linked
            this.contactRepository.save(contact);
            // @model attribute is used to send the whole object of contact
            session.setAttribute("message",new MyMessagge("Your contact is updated","success"));

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("contact name: "+contact.getName());
        System.out.println("contact ID: "+contact.getcId());


        return "redirect:/user/"+contact.getcId()+"/contact";
    }

    // your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model){
        model.addAttribute("title","Profile Page");

        return "normal/profile";
    }

    // open setting, change password
    @GetMapping("/settings")
    public String openSetting(){

        return "normal/settings";
    }

    //change password handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,HttpSession session){
        System.out.println("oldpassword"+oldPassword);
        System.out.println("newpassword"+newPassword);

        String userName= principal.getName();
        User currentUser= this.userRepository.getUserByEmail(userName);
        currentUser.getPassword();
        if(this.passwordEncoder.matches(oldPassword,currentUser.getPassword())){
            //change password
            currentUser.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message",new MyMessagge("Password has changed successfully",
                    "success"));
        }else {
            // error message
            session.setAttribute("message", new MyMessagge("Wrong Password. Please enter the correct password" +
                    "","danger"));
            return "redirect:/user/settings";

        }




        return "redirect:/user/index";
    }

}
