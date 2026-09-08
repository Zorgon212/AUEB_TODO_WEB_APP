package com.pireaus.todoWebApp.todo;

import com.pireaus.todoWebApp.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

// I use todo and task meaning the same thing!
@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Todo {
    // Basic Structure
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "description")
    private String description;
    @Column(name = "declared_time_id")
    private LocalDateTime creationDT;
    @Column(name = "completion_time_id")
    private LocalDateTime completionDT;
    @Column(name = "status")
    private boolean status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Todo() {
    }

    @Override
    public String toString() {
        return "Todo{" +
                "description='" + description + '\'' +
                ", creation datetime=" + creationDT +
                ", completion datetime=" + completionDT +
                ", isActive=" + status +
                '}';
    }

    // extra stuff needed for logic

    /** Assigns ownership and stamps the declared time - used only when a todo is first created. */
    public void setOwner(User owner) {
        this.user = owner;
        this.creationDT = LocalDateTime.now();
        this.status = false;
        this.completionDT = null;
    }

    // true if user owns task and false if user does not own it
    public boolean ownsTask(Integer userId) {
        return user != null && user.getId() != null && user.getId().equals(userId);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

//     self explanatory
    public void taskCompleted() {
        this.status = true;
        this.completionDT = LocalDateTime.now();
    }

    // returning the duration of the task if completed otherwise return null
//    public Object taskDuration() {
//        if (status == true){
//            return (Duration.between(completionDT, creationDT).toHours());
//        }
//        return null;
//    }

    // if we want to unmark the task that was supposed to be completed
    public void unCompleteTask() {
        this.status = false;
        this.completionDT = null;
    }


}
