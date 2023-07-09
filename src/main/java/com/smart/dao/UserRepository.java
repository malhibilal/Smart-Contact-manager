package com.smart.dao;

import com.smart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;


public interface UserRepository extends JpaRepository<User,Integer> {
    // this query will load the email dynamically and the user assosiated with it

    @Query("select u from User u where u.email = :email")
    User getUserByEmail(@Param("email") String email);
}






