package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest toRequest(ItemRequestDto dto, User user, LocalDateTime time) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requester(user)
                .created(time)
                .build();
    }

    public static ItemRequestDtoResponse toResponse(ItemRequest itemRequest, List<ItemDto> itemDtoList) {
        return ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtoList)
                .build();
    }
}
