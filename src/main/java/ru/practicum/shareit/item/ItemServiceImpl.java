package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.WrongAuthorException;
import ru.practicum.shareit.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndComments;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item createItem(ItemDto itemDto, Long userId) {
        checkUserExist(userId);
        User user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        return repository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(ItemDto itemDto, Long userId, Long itemId) {
        Item oldItem = repository.findById(itemId)
                        .orElseThrow(() -> new DataNotFoundException("Предмета с таким id = " + itemId + " не существует."));
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new WrongOwnerException("Только владелец может менять данные о предмете!");
        }
        User user = userService.getUserById(userId);
        Item item = ItemMapper.toItemWithId(itemDto, user, itemId);
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        repository.save(item);
        return item;
    }

    @Override
    public ItemWithBookingAndComments getItemById(Long itemId, Long userId) {
        checkUserExist(userId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Предмета с таким id = " + itemId + " не существует."));
        return convertItemToItemWithBookingAndComments(item, userId);
    }

    @Override
    public Collection<ItemWithBookingAndComments> getAllItemsByUser(Long userId) {
        checkUserExist(userId);
        return repository.findAllByOwner(userService.getUserById(userId)).stream()
                .map(item -> convertItemToItemWithBookingAndComments(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllItemsByText(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return repository.findAll().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(item -> (item.getAvailable()).equals(true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Comment createComment(CommentDtoRequest comment, Long authorId, Long itemId) {
        checkUserExist(authorId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Предмета с таким id = " + itemId + " не существует."));
        User user = userService.getUserById(authorId);
        if (bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBeforeOrderByEndDesc(authorId, itemId, LocalDateTime.now()) == null) {
            throw new WrongAuthorException("Вы не можете оставить комментарий.");
        } else {
            return commentRepository.save(CommentMapper.toComment(comment, user, item));
        }
    }

    private void checkUserExist(Long userId) {
        try {
            userService.getUserById(userId);
        } catch (RuntimeException e) {
            throw new DataNotFoundException("Пользователя с таким id = " + userId + " не существует.");
        }
    }

    private ItemWithBookingAndComments convertItemToItemWithBookingAndComments(Item item, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc(
                item.getId(), now, now);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStart(item.getId(), now);
        BookingShort last = null;
        BookingShort next = null;
        if (lastBooking != null && lastBooking.getStatus() != Status.REJECTED) {
            last = BookingMapper.toShort(lastBooking);
        }
        if (nextBooking != null && nextBooking.getStatus() != Status.REJECTED) {
            next = BookingMapper.toShort(nextBooking);
        }
        if (!userId.equals(item.getOwner().getId())) {
            last = null;
            next = null;
        }
        List<CommentDto> comments = commentRepository.findAllByItem(item).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        if (comments.isEmpty()) {
            comments = new ArrayList<>();
        }
        return ItemMapper.toItemWithBAndC(item, last, next, comments);
    }
}
