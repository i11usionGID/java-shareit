package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {
    @Autowired
    private UserService userService;
    private User user;
    private User user1;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("new@yandex.ru")
                .name("new")
                .build();
        user1 = User.builder()
                .email("old@yandex.ru")
                .name("old")
                .build();
    }

    @Test
    void getAllUsers() {
        userService.createUser(user);
        userService.createUser(user1);
        Collection<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void createUser() {
        User addUser = userService.createUser(user);
        assertEquals(user, addUser);
    }

    @Test
    void updateUser() {
        User updateUser = userService.createUser(user);
        updateUser.setName("Ivan");
        User newUser = userService.updateUser(updateUser);
        assertEquals("Ivan", newUser.getName());
        assertEquals(updateUser.getId(), newUser.getId());
    }

    @Test
    void getUserById() {
        User user2 = userService.createUser(user);
        User user3 = userService.getUserById(user2.getId());
        assertEquals(user2, user3);
    }

    @Test
    void deleteUser() {
        User user2 = userService.createUser(user);
        User user3 = userService.createUser(user1);
        userService.deleteUser(user2.getId());
        Collection<User> users = userService.getAllUsers();
        assertTrue(users.contains(user3));
        assertFalse(users.contains(user2));
    }
}
