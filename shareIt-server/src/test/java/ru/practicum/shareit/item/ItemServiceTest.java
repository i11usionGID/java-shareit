package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemWithBookingAndComments;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private ItemDto itemDto;
    private User user;
    private User requester;

    private ItemRequest itemRequest;

    private Item item;

    @BeforeEach
    void setUp() {
        requester = User.builder()
                .email("new@yandex.ru")
                .name("new")
                .build();
        userRepository.save(requester);
        user = User.builder()
                .email("old@yandex.ru")
                .name("old")
                .build();
        userRepository.save(user);
        itemRequest = ItemRequest.builder()
                .description("new request")
                .created(LocalDateTime.now())
                .requester(requester)
                .build();
        itemRequestRepository.save(itemRequest);
        itemDto = ItemDto.builder()
                .name("мышь")
                .description("игровая мышь")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
    }

    @Test
    void createItem() {
        Item item1 = itemService.createItem(itemDto, user.getId());
        assertEquals(user, item1.getOwner());
        assertEquals(requester, item1.getRequest().getRequester());
    }

    @Test
    void updateItem() {
        Item item1 = itemService.createItem(itemDto, user.getId());
        ItemDto itemDto1 = ItemDto.builder()
                .name("дрель отремонтированная")
                .description("супер дрель")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
        Item updateItem = itemService.updateItem(itemDto1, user.getId(), item1.getId());
        assertEquals(itemDto1.getName(), updateItem.getName());
        assertEquals(item1.getId(), updateItem.getId());
    }

    @Test
    void getItemById() {
        Item item1 = itemService.createItem(itemDto, user.getId());
        ItemWithBookingAndComments newItem = itemService.getItemById(item1.getId(), user.getId());
        assertTrue(newItem.getComments().isEmpty());
        assertTrue(newItem.getLastBooking() == null);

    }

    @Test
    void getAllItemsByUser() {
        itemService.createItem(itemDto, user.getId());
        List<ItemWithBookingAndComments> list = (List<ItemWithBookingAndComments>)
                itemService.getAllItemsByUser(user.getId(), 0, 5);
        assertEquals(1, list.size());
    }

    @Test
    void getAllItemsByText() {
        itemService.createItem(itemDto, user.getId());
        List<Item> items = (List<Item>) itemService.getAllItemsByText("игровая", 0, 5);
        assertEquals(itemDto.getName(), items.get(0).getName());
    }

    @Test
    void createComment() {
        Item item1 = itemService.createItem(itemDto, user.getId());
        Booking booking = Booking.builder()
                .item(item1)
                .booker(requester)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking);
        CommentDtoRequest commentDtoInput = CommentDtoRequest.builder()
                .text("text")
                .build();
        Comment comment = itemService.createComment(commentDtoInput, item1.getId(), requester.getId());
        assertEquals("text", comment.getText());
    }
}
