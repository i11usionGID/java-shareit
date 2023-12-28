package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserInMemory;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserInMemory repository;

    @Override
    public User createUser(User user) {
        return repository.create(user);
    }

    @Override
    public User updateUser(User user) {
        return repository.update(user);
    }

    @Override
    public User getUserById(Long id) {
        return repository.getById(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return repository.getAll();
    }

    @Override
    public void deleteUser(Long userId) {
        repository.delete(userId);
    }
}
