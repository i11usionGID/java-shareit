package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private Item item;
    private User user;
    private User requester;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        requester = User.builder()
                .email("new@yandex.ru")
                .name("new")
                .build();
        userRepository.save(requester);
        request = ItemRequest.builder()
                .requester(requester)
                .created(LocalDateTime.now())
                .description("description")
                .build();
        itemRequestRepository.save(request);
        user = User.builder()
                .email("old@yandex.ru")
                .name("old")
                .build();
        userRepository.save(user);
        item = Item.builder()
                .name("ноутбук")
                .description("новый ноутбук")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        itemRepository.save(item);
    }

    @Test
    void findAllByOwnerId() {
        List<Item> itemList = itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(0, 5));
        assertEquals(1, itemList.size());
        assertTrue(itemList.contains(item));
    }

    @Test
    void findAllByRequest() {
        List<Item> itemList = itemRepository.findAllByRequest(request);
        assertEquals(1, itemList.size());
        assertEquals("new@yandex.ru", itemList.get(0).getRequest().getRequester().getEmail());
    }

    @Test
    void searchWithPagination() {
        String text = "новый ноутбук";
        List<Item> itemList = itemRepository.searchWithPagination(text, PageRequest.of(0, 5));
        assertEquals(1, itemList.size());
        assertEquals(text, itemList.get(0).getDescription());
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}
