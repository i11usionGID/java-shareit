package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void getAllUsers() {
        Long userId = 1L;
        User oldUser = new User();
        oldUser.setEmail("old@yandex.ru");
        oldUser.setName("Ivan");
        oldUser.setId(userId);
        List<User> userList = new ArrayList<>();
        userList.add(oldUser);
        when(userRepository.findAll()).thenReturn(userList);
        int sizeListUsers = userService.getAllUsers().size();
        assertEquals(1, sizeListUsers, "некорректная работа.");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUserValid() {
        User userToSave = new User();
        when(userRepository.save(userToSave))
                .thenReturn(userToSave);
        User actualUser = userService.createUser(userToSave);

        assertEquals(userToSave, actualUser, "некорректная работа.");
        verify(userRepository).save(userToSave);
        verify(userRepository, times(1)).save(userToSave);
        verify(userRepository, atMost(1)).save(userToSave);
    }

    @Test
    void updateUserValid() {
        Long userId = 1L;
        User oldUser = new User();
        oldUser.setEmail("old@yandex.ru");
        oldUser.setName("Anton");
        oldUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        User newUser = new User();
        newUser.setEmail("new@yandex.ru");
        newUser.setName("new");
        newUser.setId(userId);

        oldUser = userService.updateUser(newUser);
        assertEquals(oldUser, newUser);
        verify(userRepository).save(userArgumentCaptor.capture());
        User actualUser = userArgumentCaptor.getValue();
        assertEquals("new@yandex.ru", actualUser.getEmail());
    }

    @Test
    void updateUserDataNotFoundException() {
        Long userId = 1L;
        User oldUser = new User();
        oldUser.setEmail("old@yandex.ru");
        oldUser.setName("old");
        when(userRepository.findById(userId))
                .thenThrow(DataNotFoundException.class);

        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("new@yandex.ru");
        newUser.setName("new");
        assertThrows(DataNotFoundException.class, () -> userService.updateUser(newUser));
        verify(userRepository, never()).save(newUser);
    }


    @Test
    void getUserByIdValid() {
        Long userId = 0L;
        User oldUser = new User();
        oldUser.setEmail("old@yandex.ru");
        oldUser.setName("old");
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        User user = userService.getUserById(userId);
        assertEquals(oldUser, user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByIdDataNotFoundException() {
        Long userId = 0L;
        User oldUser = new User();
        oldUser.setEmail("old@yandex.ru");
        oldUser.setName("old");
        when(userRepository.findById(userId))
                .thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void deleteUserValid() {
        User user = new User(1L, "delete", "delete@yandex.ru");
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}