package com.smart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

// entity creates automatically tables in the databases
@Entity
@Table(name="USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank(message = "name should not be blank")
    @Size(min = 2,max = 20, message = "minimum 2 and maximum 20 characters are allowed")
    private String name;
    @Column(unique = true)
    private String email;
    @Size(min= 5, message = "password must be of minimum 5 characters")
    private String password;
    private String role;
    private boolean enabled;
    private String imageUrl;
    @Column(length = 500)
    @Size(min = 10, message = "about should at least contain 1o characters")
    private String about;

    // this user has many contacts. in order to store all the contacts we can make a field list
  // one user has many contacts. that's why we use one to many annotation.
    //cascade type all means when the user is created, all the contacts associated with that user will also be created
    // Lazy means when we need contacts, we wil call and will get
    // eager means when the user is created all the contacts are fetched along with the user
    // mapping has created a third table with userid and contact id. in order to avoid this third table we can use
    // mapped by after fetch. this will mapp the contact id with user id without creating a new table
    @OneToMany(cascade = CascadeType.ALL, fetch =FetchType.LAZY,mappedBy ="user",orphanRemoval = true)
   private List<Contact> contact = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, String password,
                String role, boolean enabled, String imageUrl,
                String about, List<Contact> contact) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.imageUrl = imageUrl;
        this.about = about;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<Contact> getContact() {
        return contact;
    }

    public void setContact(List<Contact> contact) {
        this.contact = contact;
    }

   /* @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", imageUrl='" + imageUrl + '\'' +
                ", about='" + about + '\'' +
                ", contact=" + contact +
                '}';
    }*/
}
