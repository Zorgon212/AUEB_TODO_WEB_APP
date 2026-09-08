package com.pireaus.todoWebApp.todo;

import com.pireaus.todoWebApp.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// plain unit tests for the one real invariant Todo protects: completionDT is
// set if and only if status is true. No Spring context, no database.
class TodoTest {

    @Test
    void setOwnerSetsTheOwnerAndStartsAsOpen() {
        User owner = new User();
        owner.setId(1);
        Todo todo = new Todo();

        todo.setOwner(owner);

        assertThat(todo.getUser()).isEqualTo(owner);
        assertThat(todo.isStatus()).isFalse();
        assertThat(todo.getCreationDT()).isNotNull();
        assertThat(todo.getCompletionDT()).isNull();
    }

    @Test
    void taskCompletedMarksItDoneAndStampsTheCompletionTime() {
        Todo todo = new Todo();

        todo.taskCompleted();

        assertThat(todo.isStatus()).isTrue();
        assertThat(todo.getCompletionDT()).isNotNull();
    }

    @Test
    void unTaskCompletedTaskClearsTheCompletionTime() {
        Todo todo = new Todo();
        todo.taskCompleted();

        todo.unCompleteTask();

        assertThat(todo.isStatus()).isFalse();
        assertThat(todo.getCompletionDT()).isNull();
    }

    @Test
    void ownsTaskComparesTheOwnersId() {
        User owner = new User();
        owner.setId(5);
        Todo todo = new Todo();
        todo.setOwner(owner);

        assertThat(todo.ownsTask(5)).isTrue();
        assertThat(todo.ownsTask(6)).isFalse();
    }
}
