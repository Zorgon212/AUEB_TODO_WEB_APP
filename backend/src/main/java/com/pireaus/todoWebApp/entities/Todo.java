package com.pireaus.todoWebApp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Todo {








    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "description")
    private String description;

    // not sure if to keep them as datetime or int type to show in a separate datetime table
    private LocalDateTime creationDT;
    private LocalDateTime completionDT;

    @Column(name = "status")
    private boolean status;

    @Column(name= "user_id")
    private int user_id;


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
}
