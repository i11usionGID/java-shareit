package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService service;

    private static final String HEADER = "X-Sharer-User-Id";

    private User user;
    private User requester;

    private ItemRequestDto itemRequestDto;

    private ItemRequest request;

    private ItemRequestDtoResponse response;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("new@yandex.ru")
                .name("new")
                .build();
        requester = User.builder()
                .id(2L)
                .email("old@yandex.ru")
                .name("old")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(requester.getId())
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        response = ItemRequestMapper.toResponse(request, new ArrayList<>());
    }

    @SneakyThrows
    @Test
    void createRequestValid() {
        when(service.createRequest(anyLong(), any(ItemRequestDto.class), any(LocalDateTime.class)))
                .thenReturn(request);

        mockMvc.perform(post("/requests", requester.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(HEADER, "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.requester.email").value(request.getRequester().getEmail()));
    }

    @SneakyThrows
    @Test
    void createRequestItemRequestDtoNotValidException() {
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .requesterId(requester.getId())
                .description("")
                .build();
        mockMvc.perform(post("/requests", requester.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto1))
                        .header(HEADER, "2"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllRequestsValid() {
        List<ItemRequestDtoResponse> responseList = List.of(response);
        when(service.getAllRequests(user.getId())).thenReturn(responseList);

        mockMvc.perform(get("/requests", user.getId())
                        .header(HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseList.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(responseList.get(0).getDescription()));
    }

    @SneakyThrows
    @Test
    void getRequestsFromOtherUsersValid() {
        List<ItemRequestDtoResponse> responseList = List.of(response);
        when(service.getRequestsFromOtherUsers(anyLong(), anyInt(), anyInt())).thenReturn(responseList);

        mockMvc.perform(get("/requests/all", user.getId())
                        .header(HEADER, "1")
                        .param("from", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseList.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(responseList.get(0).getDescription()));
    }

    @SneakyThrows
    @Test
    void getRequestsFromOtherUsersUserIdNotValidException() {
        List<ItemRequestDtoResponse> responseList = List.of(response);
        when(service.getRequestsFromOtherUsers(anyLong(), anyInt(), anyInt())).thenReturn(responseList);

        mockMvc.perform(get("/requests/all", user.getId())
                        .header(HEADER, "q")
                        .param("from", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getItemRequestValid() {
        when(service.getItemRequest(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", user.getId())
                        .header(HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.description").value(response.getDescription()));
    }
}
