package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String HEADER = "X-Sharer-User-Id";

    private final ItemRequestService service;

    @PostMapping
    public ItemRequest createRequest(@RequestHeader(HEADER) Long userId,
                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {
        LocalDateTime now = LocalDateTime.now();
        return service.createRequest(userId, itemRequestDto, now);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getAllRequests(@RequestHeader(HEADER) Long userId) {
        return service.getAllRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getRequestsFromOtherUsers(@RequestHeader(HEADER) Long userId,
                                                                 @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                                 @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {

        return service.getRequestsFromOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getItemRequest(@PathVariable Long requestId, @RequestHeader(HEADER) Long userId) {
        return service.getItemRequest(requestId, userId);
    }

}
