package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item createItem(ItemDto itemDto, Long userId) {
        if (userId == null) {
            throw new DataNotFoundException("User id or item id not specified");
        }
        User user = userStorage.getById(userId);
        return itemStorage.create(ItemMapper.toItem(itemDto, user));
    }

    @Override
    public Item updateItem(ItemDto itemDto, Long userId, Long itemId) {
        if (itemStorage.getById(itemId).getOwner().getId() != userId) {
            throw new WrongOwnerException("Wrong user id");
        }
        if (userId == null || itemId == null) {
            throw new DataNotFoundException("User id or item id not specified");
        }
        User user = userStorage.getById(userId);
        return itemStorage.update(ItemMapper.toItemWithId(itemDto, user, itemId));
    }

    @Override
    public Item getItemById(Long itemId, Long userId) {
        if (userId == null || itemId == null) {
            throw new DataNotFoundException("User id or item id not specified");
        }
        userStorage.getById(userId);
        return itemStorage.getById(itemId);
    }

    @Override
    public Collection<Item> getAllItemsByUser(Long userId) {
        userStorage.getById(userId);
        return itemStorage.getAllByUser(userId);
    }

    @Override
    public Collection<Item> getAllItemsByText(String text) {
        return itemStorage.getAllByText(text);
    }

}
