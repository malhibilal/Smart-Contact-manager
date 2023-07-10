    package com.smart.entities;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;

    @Entity
    @Table(name= "CONTACT")
    public class Contact {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int cId;
        private String name;
        private String secondName;
        private String work;
        private String email;
        private String phone; // we can change it afterwards and we dont need to perform any calculation@
        @Column(name = "image")
        private String  image;
        private String description;

        // to do bidirectional mapping with the user
        @ManyToOne
        @JsonIgnore
        private User user;

        public Contact() {
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getcId() {
            return cId;
        }

        public void setcId(int cId) {
            this.cId = cId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public String getWork() {
            return work;
        }

        public void setWork(String work) {
            this.work = work;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }





        public void setPhone(String phone) {
            this.phone = phone;
        }




        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "Contact{" +
                    "cId=" + cId +
                    ", name='" + name + '\'' +
                    ", secondName='" + secondName + '\'' +
                    ", work='" + work + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", ProfileImage='" + image + '\'' +
                    ", description='" + description + '\'' +
                    ", user=" + user +
                    '}';
        }
    }
