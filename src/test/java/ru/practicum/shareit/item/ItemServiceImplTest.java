package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndComments;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemDto itemDto;
    private User user;

    private ItemRequest itemRequest;

    private Item item;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("ноутбук")
                .description("новый ноутбук")
                .available(true)
                .requestId(1L)
                .build();
        user = User.builder()
                .id(1L)
                .email("new@yandex.ru")
                .name("new")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("new request")
                .created(LocalDateTime.now())
                .requester(new User())
                .build();
        item = Item.builder()
                .id(1L)
                .name("мышь")
                .description("игровая мышь")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
    }

    @Test
    void createItemValid() {
        Item actualItem = ItemMapper.toItem(itemDto, user, itemRequest);

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(ItemMapper.toItem(itemDto, user, itemRequest))).thenReturn(actualItem);

        Item itemReturn = itemService.createItem(itemDto, user.getId());
        assertEquals(user.getId(), itemReturn.getOwner().getId(), "метод отработал некорректно");
        assertEquals(itemRequest.getCreated(), itemReturn.getRequest().getCreated(), "метод отработал некорректно");
    }

    @Test
    void createItemUserDataNotFoundException() {
        Long userId = 222L;
        User notValidUser = new User();
        ItemDto itemDto1 = ItemDto.builder()
                .id(100L)
                .name("мышь")
                .description("игровая мышь")
                .available(true)
                .requestId(1L)
                .build();
        ItemRequest itemRequest = new ItemRequest();

        when(userService.getUserById(userId)).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.createItem(itemDto1, userId));
        verify(itemRepository, never()).save(ItemMapper.toItem(itemDto1, notValidUser, itemRequest));
    }

    @Test
    void createItemItemNotValidException() {
        User user1 = new User();
        user1.setId(1L);
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);

        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRequestRepository.findById(itemRequest1.getId())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.createItem(itemDto, user1.getId()));
        verify(itemRepository, never()).save(ItemMapper.toItem(itemDto, user1, itemRequest1));
    }

    @Test
    void updateItemValid() {
        Item item = ItemMapper.toItem(itemDto, user, itemRequest);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenReturn(user);

        Item updateItem = itemService.updateItem(itemDto, user.getId(), item.getId());
        assertEquals(user.getEmail(), updateItem.getOwner().getEmail(), "метод отработал некорректно");
        verify(itemRepository, times(1)).save(ItemMapper.toItemWithId(itemDto, user, item.getId()));
    }

    @Test
    void updateItemItemNotValidException() {
        Item notValidItem = new Item();
        notValidItem.setId(1L);
        Long userId = 1L;
        User user1 = new User();

        when(itemRepository.findById(notValidItem.getId())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.updateItem(itemDto, userId, notValidItem.getId()));
        verify(itemRepository, never()).save(ItemMapper.toItemWithId(itemDto, user1, notValidItem.getId()));
    }

    @Test
    void updateItemNotValidException() {
        Item item = ItemMapper.toItem(itemDto, user, itemRequest);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.updateItem(itemDto, user.getId(), item.getId()));
        verify(itemRepository, never()).save(ItemMapper.toItemWithId(itemDto, user, item.getId()));
    }

    @Test
    void getItemByIdValid() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemWithBookingAndComments item1 = itemService.getItemById(item.getId(), user.getId());
        assertNotNull(item1);
        assertEquals(item.getDescription(), item1.getDescription(), "метод отработал некорректно");
        assertTrue(item1.getName().equals("мышь"));
    }

    @Test
    void getItemByIdItemDataNotFoundException() {
        when(itemRepository.findById(item.getId())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.getItemById(item.getId(), user.getId()));
    }

    @Test
    void getAllItemsByUserValid() {
        List<ItemWithBookingAndComments> list = new ArrayList<>();
        BookingShort lastBooking = BookingShort.builder()
                .id(1L)
                .bookerId(2L)
                .build();
        BookingShort nextBooking = BookingShort.builder()
                .id(2L)
                .bookerId(3L)
                .build();
        ItemWithBookingAndComments item1 = ItemWithBookingAndComments.builder()
                .id(1L)
                .name("!!!")
                .description("QWERTY")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(new ArrayList<>())
                .build();
        list.add(item1);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Pageable pageable = PageRequest.of(15 / 10, 10);

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findAllByOwnerId(user.getId(), pageable)).thenReturn(itemList);

        List<ItemWithBookingAndComments> item2 = (List<ItemWithBookingAndComments>)
                itemService.getAllItemsByUser(user.getId(), 15, 10);
        assertFalse(item2.isEmpty());
        assertEquals(1, item2.size(), "метод отработал некорректно");
        assertEquals("игровая мышь", item2.get(0).getDescription(), "метод отработал некорректно");
    }

    @Test
    void getAllItemsByUserNotValidException() {
        Integer from = 5;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from / size, size);

        when(userService.getUserById(user.getId())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.getAllItemsByUser(user.getId(), from, size));
        verify(itemRepository, never()).findAllByOwnerId(user.getId(), pageable);
    }

    @Test
    void getAllItemsByTextValid() {
        String text = "удобный";
        Integer from = 1;
        Integer size = 10;
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Pageable pageable = PageRequest.of(from / size, size);

        when(itemRepository.searchWithPagination(text, pageable)).thenReturn(itemList);

        List<Item> actualItemList = (List<Item>) itemService.getAllItemsByText(text, from, size);
        assertFalse(actualItemList.isEmpty());
        assertEquals(1, actualItemList.size(), "метод отработал некорректно");
        assertEquals(item, actualItemList.get(0), "метод отработал некорректно");
    }

    @Test
    void createCommentValid() {
        CommentDtoRequest commentDtoInput = CommentDtoRequest.builder()
                .text("отличная вещь")
                .build();
        User user1 = User.builder()
                .id(2L)
                .email("new@yandex.ru")
                .name("new")
                .build();
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user1)
                .start(dateTime)
                .end(dateTime.plusDays(1))
                .status(Status.WAITING)
                .build();
        Comment comment = CommentMapper.toComment(commentDtoInput, user, item);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBeforeOrderByEndDesc(any(Long.class),
                any(Long.class), any(LocalDateTime.class))).thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocationOnMock -> {
            comment.setId(1L);
            return comment;
        });

        Comment newComment = itemService.createComment(commentDtoInput, user.getId(), item.getId());
        assertFalse(newComment == null);
        assertEquals(commentDtoInput.getText(), newComment.getText(), "метод отработал некорректно");
    }

    @Test
    void createCommentUserIdNotValidException() {
        Long itemId = 1L;
        CommentDtoRequest commentDtoInput = CommentDtoRequest.builder()
                .text("отличная вещь")
                .build();

        when(userService.getUserById(user.getId())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.createComment(commentDtoInput, user.getId(), itemId));
    }

    @Test
    void createCommentItemIdNotValidException() {
        CommentDtoRequest commentDtoInput = CommentDtoRequest.builder()
                .text("отличная вещь")
                .build();

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.createComment(commentDtoInput, user.getId(), item.getId()));
    }
}