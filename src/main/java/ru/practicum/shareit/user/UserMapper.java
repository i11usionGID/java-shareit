package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User toUserWithId(UserDto userDto, Long id) {
        return User.builder()
                .id(id)
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}