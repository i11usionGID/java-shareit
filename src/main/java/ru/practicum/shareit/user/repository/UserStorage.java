package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getAll();

    User create(User user);

    User update(User user);

    User getById(Long userId);

    void delete(Long userId);

}