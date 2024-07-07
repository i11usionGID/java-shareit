package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoResponse getItemRequest(Long requestId, Long userId);

    List<ItemRequestDtoResponse> getAllRequests(Long userId);

    List<ItemRequestDtoResponse> getRequestsFromOtherUsers(Long userId, Integer from, Integer size);

    ItemRequest createRequest(Long userId, ItemRequestDto request, LocalDateTime time);
}
