package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class Item {
    private Long id;
    private User owner;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}
