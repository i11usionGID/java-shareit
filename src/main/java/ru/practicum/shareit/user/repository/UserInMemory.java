package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DataAlreadyExistException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Repository
public class UserInMemory implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    public User create(User user) {
        for (User users : users.values()) {
            if (users.getEmail().equals(user.getEmail())) {
                throw new DataAlreadyExistException("Not unique email.");
            }
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        int count = 0;
        for (User users: users.values()) {
            if (users.getEmail().equals(user.getEmail()) && !users.getId().equals(user.getId())) {
                count++;
            }
        }
        if (count > 0) {
            throw new DataAlreadyExistException("Not unique email.");
        }
        if (user.getName() != null) {
            users.get(user.getId()).setName(user.getName());
        }
        if (user.getEmail() != null) {
            users.get(user.getId()).setEmail(user.getEmail());
        }
        return users.get(user.getId());
    }

    public User getById(Long id) {
        checkUserExist(id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void delete(Long userId) {
        checkUserExist(userId);
        users.remove(userId);
    }

    private void checkUserExist(Long id) {
        if (!users.containsKey(id)) {
            throw new DataNotFoundException("User with this id not exist in memory.");
        }
    }
}
