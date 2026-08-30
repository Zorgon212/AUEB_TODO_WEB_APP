package com.pireaus.todoWebApp.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    public enum UserCategory{
            USER,ADMIN,GUEST
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "status")
    private boolean status;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private String type;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Todo> todos = new ArrayList<>();

    public User(){
    }




    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + "*".repeat(password.length()) + '\'' + // I hid the password
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
