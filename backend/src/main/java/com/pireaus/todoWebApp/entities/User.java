package com.pireaus.todoWebApp.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Integer id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "email")
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;
    @Column(name = "status")
    private boolean status;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserCategory type;

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
                ", password='" + "********"+ '\'' + // I hid the password
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
