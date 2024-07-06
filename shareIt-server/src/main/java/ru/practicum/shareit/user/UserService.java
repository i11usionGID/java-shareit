package ru.practicum.shareit.user;


import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(Long userId);

    void deleteUser(Long userId);
}
