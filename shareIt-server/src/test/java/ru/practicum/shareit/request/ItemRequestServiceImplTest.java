package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private User userOwner;
    private User userBooker;

    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        userOwner = User.builder()
                .id(1L)
                .email("new@yandex.ru")
                .name("new")
                .build();
        userBooker = User.builder()
                .id(2L)
                .email("new@yandex.ru")
                .name("new")
                .build();
        item = Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("новый ноутбук")
                .available(true)
                .owner(userOwner)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(userOwner)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getItemRequestValid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        ItemRequestDtoResponse itemRequestResponse = itemRequestService.getItemRequest(itemRequest.getId(), userOwner.getId());
        assertTrue(itemRequestResponse != null);
        assertEquals(itemRequest.getId(), itemRequestResponse.getId(), "некорректно отработал метод");
        assertEquals(itemRequest.getCreated(), itemRequestResponse.getCreated(), "некорректно отработал метод");
        assertEquals(itemRequest.getDescription(), itemRequestResponse.getDescription(), "некорректно отработал метод");
    }

    @Test
    void getItemRequestUseridNotValidException() {
        when(userRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> itemRequestService.getItemRequest(itemRequest.getId(), userOwner.getId()));
    }

    @Test
    void getItemRequestRequestIdNotValidException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> itemRequestService.getItemRequest(itemRequest.getId(), userOwner.getId()));
    }

    @Test
    void getAllRequestsValid() {
        List<ItemRequest> list = List.of(itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findAllByRequesterOrderByCreatedDesc(any(User.class))).thenReturn(list);
        List<ItemRequestDtoResponse> itemRequestList = itemRequestService.getAllRequests(userOwner.getId());
        assertFalse(itemRequestList.isEmpty());
        assertEquals(list.get(0).getId(), itemRequestList.get(0).getId(), "Некорректно отработал метод");
        assertEquals(list.get(0).getDescription(), itemRequestList.get(0).getDescription(), "Некорректно отработал метод");
        assertEquals(list.get(0).getCreated(), itemRequestList.get(0).getCreated(), "Некорректно отработал метод");
    }


    @Test
    void getAllRequestsUserIdNotValidException() {
        when(userRepository.findById(userOwner.getId()))
                .thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> itemRequestService.getAllRequests(userOwner.getId()));
    }

    @Test
    void getRequestsFromOtherUsersValid() {
        List<ItemRequest> list = List.of(itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findAllByRequesterNotOrderByCreatedDesc(any(User.class), any(Pageable.class)))
                .thenReturn(list);
        List<ItemRequestDtoResponse> itemRequestResponseList = itemRequestService.getRequestsFromOtherUsers(userOwner.getId(), 1, 5);
        assertFalse(itemRequestResponseList.isEmpty());
        assertEquals(list.get(0).getId(), itemRequestResponseList.get(0).getId(), "Некорректно отработал метод");
        assertEquals(list.get(0).getDescription(), itemRequestResponseList.get(0).getDescription(), "Некорректно отработал метод");
        assertEquals(list.get(0).getCreated(), itemRequestResponseList.get(0).getCreated(), "Некорректно отработал метод");
    }

    @Test
    void getRequestsFromOtherUsersNotValidException() {
        when(userRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> itemRequestService.getRequestsFromOtherUsers(userOwner.getId(), 1, 5));
    }

    @Test
    void createRequestValid() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("new request", 2L);
        ItemRequest itemRequest = new ItemRequest(1L, "description", userBooker, now);
        when(userRepository.findById(userBooker.getId()))
                .thenReturn(Optional.of(userBooker));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequest itemRequest1 = itemRequestService.createRequest(userBooker.getId(), itemRequestDto, now);
        assertEquals(userBooker.getId(), itemRequest1.getRequester().getId(), "Неверно отработал метод");
        assertEquals("description", itemRequest1.getDescription(), "Неверно отработал метод");
        assertEquals(now, itemRequest1.getCreated());
    }

    @Test
    void createRequestDataNotFoundException() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("new request", 2L);
        when(userRepository.findById(userBooker.getId()))
                .thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> itemRequestService.createRequest(userBooker.getId(), itemRequestDto, now));
    }
}