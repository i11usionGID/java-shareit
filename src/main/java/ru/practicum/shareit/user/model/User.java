package ru.practicum.shareit.user.model;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class User {
    Long id;
    String name;
    String email;
}
