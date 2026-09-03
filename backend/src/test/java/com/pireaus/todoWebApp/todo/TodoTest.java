package com.pireaus.todoWebApp.todo;

import com.pireaus.todoWebApp.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// plain unit tests for the one real invariant Todo protects: completionDT is
// set if and only if status is true. No Spring context, no database.
class TodoTest {

    @Test
    void assignToSetsTheOwnerAndStartsAsOpen() {
        User owner = new User();
        owner.setId(1);
        Todo todo = new Todo();

        todo.assignTo(owner);

        assertThat(todo.getUser()).isEqualTo(owner);
        assertThat(todo.isStatus()).isFalse();
        assertThat(todo.getCreationDT()).isNotNull();
        assertThat(todo.getCompletionDT()).isNull();
    }

    @Test
    void completeMarksItDoneAndStampsTheCompletionTime() {
        Todo todo = new Todo();

        todo.complete();

        assertThat(todo.isStatus()).isTrue();
        assertThat(todo.getCompletionDT()).isNotNull();
    }

    @Test
    void reopenClearsTheCompletionTime() {
        Todo todo = new Todo();
        todo.complete();

        todo.reopen();

        assertThat(todo.isStatus()).isFalse();
        assertThat(todo.getCompletionDT()).isNull();
    }

    @Test
    void belongsToComparesTheOwnersId() {
        User owner = new User();
        owner.setId(5);
        Todo todo = new Todo();
        todo.assignTo(owner);

        assertThat(todo.belongsTo(5)).isTrue();
        assertThat(todo.belongsTo(6)).isFalse();
    }
}
