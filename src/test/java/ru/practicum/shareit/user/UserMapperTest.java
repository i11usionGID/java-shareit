package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Ivan", "new@yandex.ru");
    }

    @Test
    void toDto() {
        UserDto userDto = UserMapper.toDto(user);
        assertEquals("new@yandex.ru", userDto.getEmail(), "некорректная работа.");
        assertEquals("Ivan", userDto.getName(), "некорректная работа.");
    }

    @Test
    void toUser() {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .email("old@yandex.ru")
                .name("Anton")
                .build();
        User actualUser = UserMapper.toUser(userDto);
        assertEquals("old@yandex.ru", actualUser.getEmail(), "некорректная работа.");
        assertEquals("Anton", actualUser.getName(), "некорректная работа.");
    }

    @Test
    void toUserWithId() {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .email("oldnew@yandex.ru")
                .name("abbadon")
                .build();
        User newUser = UserMapper.toUserWithId(userDto, userDto.getId());
        assertEquals("oldnew@yandex.ru", newUser.getEmail(), "некорректная работа.");
        assertEquals("abbadon", newUser.getName(), "некорректная работа.");
        assertEquals(2L, newUser.getId(), "некорректная работа.");
    }
}
