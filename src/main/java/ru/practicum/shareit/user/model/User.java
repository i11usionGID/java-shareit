package ru.practicum.shareit.user.model;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}
