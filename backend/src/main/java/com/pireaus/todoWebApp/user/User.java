package com.pireaus.todoWebApp.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pireaus.todoWebApp.todo.Todo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate root of the "user" boundary. A User owns its Todos - deleting a
 * user deletes all of their todos too (cascade + orphanRemoval below), which
 * is the one real consistency boundary this aggregate protects.
 */
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

    // deleting a user deletes their todos too (admin "delete user" feature)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Todo> todos = new ArrayList<>();

    public User(){
    }

    // --- domain behaviour --------------------------------------------------
    // intention-revealing operations, so callers say *what* they mean
    // (promote, change password, ...) instead of poking setters directly.

    public boolean isAdmin() {
        return type == UserCategory.ADMIN;
    }

    public boolean isSameUserAs(Integer otherId) {
        return id != null && id.equals(otherId);
    }

    /** Expects an already-encoded password - hashing is an infrastructure concern, not the entity's. */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void promoteTo(UserCategory newType) {
        this.type = newType;
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
