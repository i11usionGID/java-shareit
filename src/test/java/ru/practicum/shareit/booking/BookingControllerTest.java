package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final String HEADER = "X-Sharer-User-Id";
    private Booking booking;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResp;
    private Item item;
    private User bookingUser;
    private User user;
    private ItemRequest itemRequest;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemRequest = itemRequestBuilder();
        bookingUser = bookingUserBuilder();
        user = userBuilder();
        item = itemBuilder();
        itemDto = ItemMapper.toDto(item);
        bookingDtoRequest = bookingDtoRequestBuilder();
        booking = BookingMapper.toBooking(bookingDtoRequest, bookingUser, item, Status.WAITING);
        booking.setId(0L);
        bookingDtoResp = BookingMapper.toResponse(booking);

    }

    @SneakyThrows
    @Test
    void addBookingInputValid() {
        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong())).thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void addBookingInputNotValidException() {
        bookingDtoRequest.setEnd(LocalDateTime.now().minusDays(1));
        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong()))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, "1"))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).createBooking(any(BookingDtoRequest.class), anyLong());
    }

    @SneakyThrows
    @Test
    void changeStatusOfBooking() {
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingUser.getId())
                        .header(HEADER, "1")
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(0));
    }

    @SneakyThrows
    @Test
    void getBookingInputValueValid() {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResp.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(Status.WAITING)));
    }

    @SneakyThrows
    @Test
    void getAllBookersInputValueValid() {
        List<Booking> list = List.of(booking);
        when(bookingService.getAllBookingsByUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, "1")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(list.get(0).getId()))
                .andExpect(jsonPath("$[0].status").value(String.valueOf(list.get(0).getStatus())))
                .andExpect(jsonPath("$[0].booker.id").value(String.valueOf(list.get(0).getBooker().getId())));
    }

    @SneakyThrows
    @Test
    void getAllBookersInputValueNotValidException() {
        List<Booking> list = List.of(booking);
        when(bookingService.getAllBookingsByUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, "p")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllBookingsItemsByOwnerInputValueValid() {
        List<Booking> list = List.of(booking);
        when(bookingService.getAllBookingsItemsByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings/owner", bookingUser.getId())
                        .header(HEADER, "1")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "15"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(list.get(0).getId()))
                .andExpect(jsonPath("$[0].booker.id").value(list.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].item.name").value(list.get(0).getItem().getName()));
        verify(bookingService, times(1)).getAllBookingsItemsByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllBookingsItemsByOwnerInputValueNotValidException() {
        List<Booking> list = List.of(booking);
        when(bookingService.getAllBookingsItemsByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings/owner", bookingUser.getId())
                        .header(HEADER, "q")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "15"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private ItemRequest itemRequestBuilder() {
        ItemRequest itRequest = ItemRequest.builder()
                .id(1L)
                .description("new request")
                .created(LocalDateTime.now())
                .requester(new User())
                .build();
        return itRequest;
    }

    private User bookingUserBuilder() {
        User user = User.builder()
                .id(1L)
                .email("mail@yandex.ru")
                .name("Ivan")
                .build();
        return user;
    }

    private User userBuilder() {
        User user = User.builder()
                .id(2L)
                .email("newmail@yandex.ru")
                .name("Anton")
                .build();
        return user;
    }

    private Item itemBuilder() {
        Item item = Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("мощный ноутбук")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        return item;
    }

    private BookingDtoRequest bookingDtoRequestBuilder() {
        BookingDtoRequest data = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        return data;
    }
}
