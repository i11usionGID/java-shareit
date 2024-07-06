package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

    private User user1;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    private LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("new@yandex.ru")
                .name("new")
                .build();
        userRepository.save(user);
        user1 = User.builder()
                .email("old@yandex.ru")
                .name("old")
                .build();
        userRepository.save(user1);
        itemRequest = ItemRequest.builder()
                .description("description1")
                .created(now)
                .requester(user)
                .build();
        itemRequestRepository.save(itemRequest);
        itemRequest1 = ItemRequest.builder()
                .description("description2")
                .created(now.minusHours(1))
                .requester(user)
                .build();
        itemRequestRepository.save(itemRequest1);
        itemRequest2 = ItemRequest.builder()
                .description("description3")
                .created(now.minusHours(2))
                .requester(user)
                .build();
        itemRequestRepository.save(itemRequest2);
    }


    @Test
    void findAllByRequesterOrderByCreatedDesc() {
        List<ItemRequest> list = itemRequestRepository.findAllByRequesterOrderByCreatedDesc(user);
        assertEquals(3, list.size(), "некорректная работа.");
        assertEquals(itemRequest2, list.get(2), "некорректная работа.");
        assertEquals(itemRequest, list.get(0), "некорректная работа.");
        assertEquals(itemRequest1, list.get(1), "некорректная работа.");
    }

    @Test
    void findAllByRequesterNotOrderByCreatedDesc() {
        List<ItemRequest> list = itemRequestRepository.findAllByRequesterNotOrderByCreatedDesc(user1, PageRequest.of(0, 2));
        List<ItemRequest> list1 = itemRequestRepository.findAllByRequesterNotOrderByCreatedDesc(user1, PageRequest.of(0, 5));
        assertEquals(2, list.size(), "некорректная работа.");
        assertEquals(3, list1.size(), "некорректная работа.");
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}