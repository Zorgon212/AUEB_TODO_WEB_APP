package com.pireaus.todoWebApp.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// plain unit tests for the domain behaviour on the User aggregate root -
// no Spring context, no database, just the entity's own logic.
class UserTest {

    @Test
    void isAdminIsTrueOnlyForTheAdminRole() {
        User user = new User();

        user.promoteTo(User.UserCategory.ADMIN);
        assertThat(user.isAdmin()).isTrue();

        user.promoteTo(User.UserCategory.USER);
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    void isSameUserAsComparesById() {
        User user = new User();
        user.setId(7);

        assertThat(user.isSameUserAs(7)).isTrue();
        assertThat(user.isSameUserAs(8)).isFalse();
    }

    @Test
    void isSameUserAsIsFalseBeforeTheUserHasAnId() {
        User user = new User();

        assertThat(user.isSameUserAs(1)).isFalse();
    }

    @Test
    void changePasswordStoresExactlyWhatItIsGiven() {
        User user = new User();

        user.changePassword("already-encoded-value");

        assertThat(user.getPassword()).isEqualTo("already-encoded-value");
    }
}
