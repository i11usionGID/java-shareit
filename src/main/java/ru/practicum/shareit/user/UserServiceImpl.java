package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;

import javax.transaction.Transactional;
import java.util.Collection;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public User createUser(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        checkUserExist(user.getId());
        User oldUser = getUserById(user.getId());
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        repository.save(user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return repository.findById(id)
                        .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + id + " не существует."));
    }

    @Override
    public Collection<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    private void checkUserExist(Long userId) {
        if (!repository.existsById(userId)) {
            throw new DataNotFoundException("Пользователя с таким id = " + userId + " не существует.");
        }
    }
}