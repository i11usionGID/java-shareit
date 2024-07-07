package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto request) {
        User user = UserMapper.toUser(request);
        return UserMapper.toDto(service.createUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto request,
                              @PathVariable(value = "userId") Long id) {
        User user = UserMapper.toUserWithId(request, id);
        return UserMapper.toDto(service.updateUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable(value = "userId") Long id) {
        return UserMapper.toDto(service.getUserById(id));
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return service.getAllUsers().stream()
                .map(s1 -> UserMapper.toDto(s1))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(value = "userId") Long id) {
        service.deleteUser(id);
    }
}