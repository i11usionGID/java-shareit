package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest createRequest(Long userId, ItemRequestDto request, LocalDateTime time) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        ItemRequest itemRequest = ItemRequestMapper.toRequest(request, user, time);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequestDtoResponse getItemRequest(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + userId + " не существует."));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("Запроса с таким id = " + requestId + "  не существует"));
        return ItemRequestMapper.toResponse(itemRequest, findItemsByRequest(itemRequest));
    }

    @Override
    public List<ItemRequestDtoResponse> getAllRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        return itemRequestRepository.findAllByRequesterOrderByCreatedDesc(user)
                .stream()
                .map(itemRequest -> ItemRequestMapper.toResponse(itemRequest, findItemsByRequest(itemRequest)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoResponse> getRequestsFromOtherUsers(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        Pageable page = PageRequest.of(from, size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterNotOrderByCreatedDesc(user, page);
        return requests.stream()
                .map(itemRequest -> ItemRequestMapper.toResponse(itemRequest, findItemsByRequest(itemRequest)))
                .collect(Collectors.toList());
    }

    public List<ItemDto> findItemsByRequest(ItemRequest request) {
        return itemRepository.findAllByRequest(request).stream()
                .map(item -> ItemMapper.toDto(item))
                .collect(Collectors.toList());
    }
}
