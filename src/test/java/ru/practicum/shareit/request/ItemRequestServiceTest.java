package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private User userOwner;
    private User requester;

    private Item item;

    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDto1;

    @BeforeEach
    void setUp() {
        userOwner = User.builder()
                .email("userOwner@yandex.ru")
                .name("userOwner")
                .build();
        userRepository.save(userOwner);
        requester = User.builder()
                .email("userBooker@yandex.ru")
                .name("userBooker")
                .build();
        userRepository.save(requester);
        item = Item.builder()
                .name("Дрель")
                .description("Очень хорошая дрель")
                .available(true)
                .owner(userOwner)
                .build();
        itemRepository.save(item);
        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(requester.getId())
                .build();
        itemRequestDto1 = ItemRequestDto.builder()
                .description("new description")
                .requesterId(requester.getId())
                .build();
    }

    @Test
    void addRequest() {
        ItemRequest itemRequest1 = itemRequestService.createRequest(requester.getId(), itemRequestDto, now);
        assertEquals("description", itemRequest1.getDescription());
    }

    @Test
    void getAllMyRequests() {
        itemRequestService.createRequest(requester.getId(), itemRequestDto, now);
        itemRequestService.createRequest(requester.getId(), itemRequestDto1, now);
        List<ItemRequestDtoResponse> responseList = itemRequestService.getAllRequests(requester.getId());
        assertEquals(2, responseList.size());
    }

    @Test
    void getListOfOtherUsersRequests() {
        itemRequestService.createRequest(requester.getId(), itemRequestDto, now);
        itemRequestService.createRequest(requester.getId(), itemRequestDto1, now);
        List<ItemRequestDtoResponse> responseList = itemRequestService.getRequestsFromOtherUsers(userOwner.getId(), 0, 1);
        assertEquals(1, responseList.size());
    }

    @Test
    void getItemRequest() {
        ItemRequest itemRequest1 = itemRequestService.createRequest(requester.getId(), itemRequestDto, now);
        ItemRequest itemRequest2 = itemRequestService.createRequest(requester.getId(), itemRequestDto1, now);
        ItemRequestDtoResponse itemRequestResponse = itemRequestService.getItemRequest(itemRequest1.getId(), requester.getId());
        assertEquals("description", itemRequestResponse.getDescription());
    }
}