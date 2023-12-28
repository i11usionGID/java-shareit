package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class Item {
    Long id;
    User owner;
    String name;
    String description;
    Boolean available;
    ItemRequest request;
}
