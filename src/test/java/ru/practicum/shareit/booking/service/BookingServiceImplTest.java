package ru.practicum.shareit.booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void getBookingById_whenBookingCreated_thenReturnBooking() {
        long bookingId = 1L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        Item item = new Item();

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);

        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingInfoDto actualBookingInfoDto = bookingService.getBookingById(userId, bookingId);
        assertEquals(bookingInfoDto, actualBookingInfoDto);
    }

    @Test
    void getBookingByUserId_whenUserCreateBooking_thenReturnBookingList() {
        long bookerId = 1L;
        String stateParam = "ALL";
        BookingState.checkState(stateParam);
        int from = 2;
        int size = 5;

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(0, size, sort);

        Booking booking = new Booking();
        User booker = new User();
        booking.setBooker(booker);
        Item item = new Item();
        User owner = new User();
        item.setOwner(owner);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, pageRequest, bookings.size());
        List<BookingInfoDto> bookingInfoDtoList = bookings.stream()
            .map(BookingMapper::toBookingInfoDto)
            .collect(Collectors.toList());


        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_Id(bookerId, pageRequest)).thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByUserId(bookerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList.size(), resultBookingInfoDtoList.size());
    }

    @Test
    void getBookingByOwnerId() {
        long ownerId = 1L;
        String stateParam = "ALL";
        BookingState.checkState(stateParam);
        int from = 2;
        int size = 5;

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(0, size, sort);

        Booking booking = new Booking();
        User booker = new User();
        booking.setBooker(booker);
        Item item = new Item();
        User owner = new User();
        item.setOwner(owner);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, pageRequest, bookings.size());
        List<BookingInfoDto> bookingInfoDtoList = bookings.stream()
            .map(BookingMapper::toBookingInfoDto)
            .collect(Collectors.toList());


        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItem_Owner_Id(ownerId, pageRequest))
            .thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByOwnerId(ownerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList.size(), resultBookingInfoDtoList.size());
    }

    @Test
    void createBooking_whenBookingValid_thenReturnBooking() {
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);

        User owner = new User();
        owner.setId(2L);

        item.setOwner(owner);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(5));

        Booking booking = BookingMapper.toBooking(bookingDto);

        booking.setBooker(user);
        booking.setItem(item);

        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingInfoDto actualBookingInfoDto = bookingService.createBooking(userId, bookingDto);

        assertEquals(bookingInfoDto.getId(), actualBookingInfoDto.getId());
    }

    @Test
    void approveBooking() {
        long ownerId = 1L;
        long bookingId = 1L;

        Item item = new Item();
        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);
        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingInfoDto actualBookingInfoDto = bookingService.approveBooking(ownerId, bookingId, true);

        assertEquals(BookingStatus.APPROVED, actualBookingInfoDto.getStatus());
    }
}