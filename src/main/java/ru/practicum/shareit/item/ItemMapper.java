package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndComments;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .owner(user)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItemWithId(ItemDto itemDto, User user, Long itemId) {
        return Item.builder()
                .id(itemId)
                .owner(user)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemWithBookingAndComments toItemWithBAndC(Item item, BookingShort last, BookingShort next,
                                                             List<CommentDto> comments) {
        return ItemWithBookingAndComments.builder()
                .id(item.getId())
                .available(item.getAvailable())
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .build();
    }
}
