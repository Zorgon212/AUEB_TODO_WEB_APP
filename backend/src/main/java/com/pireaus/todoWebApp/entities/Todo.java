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
    private Integer id;
    @Column(name = "description")
    private String description;

    // remember to change later
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
}
