package com.pireaus.todoWebApp.todo;

import com.pireaus.todoWebApp.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// don't get confused, todo and task are the same thing!!
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

    // --- domain behaviour --------------------------------------------------

    /** Assigns ownership and stamps the declared time - used only when a todo is first created. */
    public void assignTo(User owner) {
        this.user = owner;
        this.creationDT = LocalDateTime.now();
        this.status = false;
        this.completionDT = null;
    }

    public boolean belongsTo(Integer userId) {
        return user != null && user.getId() != null && user.getId().equals(userId);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    /**
     * The one real invariant this entity protects: completionDT is set if
     * and only if status is true. Every caller goes through complete()/
     * reopen() instead of poking status/completionDT directly, so the two
     * fields can never drift out of sync.
     */
    public void complete() {
        this.status = true;
        this.completionDT = LocalDateTime.now();
    }

    public void reopen() {
        this.status = false;
        this.completionDT = null;
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
