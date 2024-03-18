package ru.practicum.shareit.booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongOwnerException;
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
    void getBookingById_whenBookingNotCreated_thenReturnException() {
        long bookingId = 1L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
            () -> bookingService.getBookingById(bookingId, userId));

        assertEquals("Не найдена аренда с id: " + bookingId, ex.getMessage());
    }

    @Test
    void getBookingById_whenUserIdEqualsBookerId_thenReturnedBooking() {
        long userId = 1L;
        long bookingId = 1L;

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingInfoDto actualBookingInfoDto = bookingService.getBookingById(userId, bookingId);
        assertEquals(bookingInfoDto, actualBookingInfoDto);
    }

    @Test
    void findByBookingId_whenUserIdNotEqualsBookerId_thenThrowException() {
        long userId = 1L;
        long bookingId = 1L;

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        User owner = new User();
        owner.setId(3);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        WrongOwnerException ex = assertThrows(WrongOwnerException.class,
            () -> bookingService.getBookingById(userId, bookingId));

        assertEquals("Доступно только для владельца вещи или автора аренды", ex.getMessage());
    }

    @Test
    void getBookingByUserId_whenBookingStateAll_thenReturnBookingList() {
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
    void getBookingByUserId_whenStateCurrent_thenReturnBookingList() {
        long bookerId = 1L;
        String stateParam = "current";
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
        when(bookingRepository.findByBooker_IdAndStartLessThanEqualAndEndAfter(anyLong(), any(LocalDateTime.class),
            any(LocalDateTime.class), any(PageRequest.class)))
            .thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByUserId(bookerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByUserId_whenBookingStatePast_thenReturnBookingList() {
        long bookerId = 1L;
        String stateParam = "past";
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
        when(bookingRepository.findByBooker_IdAndEndBefore(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
            .thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByUserId(bookerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByUserId_whenBookingStateFuture_thenReturnBookingList() {
        long bookerId = 1L;
        String stateParam = "future";
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
        when(bookingRepository
            .findByBooker_IdAndStartAfter(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
            .thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByUserId(bookerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByUserId_whenStateWaiting_thenReturnBookingList() {
        long bookerId = 1L;
        String stateParam = "waiting";
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
        when(bookingRepository.findByBooker_IdAndStatusEquals(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
            .thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByUserId(bookerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByUserId_whenStateRejected_thenReturnBookingList() {
        long bookerId = 1L;
        String stateParam = "rejected";
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
        when(bookingRepository.findByBooker_IdAndStatusEquals(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
            .thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByUserId(bookerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByUserId_whenStateUnknown_thenThrowIllegalStateException() {
        long bookerId = 1L;
        String stateParam = "unknown";
        int from = 2;
        int size = 5;

        User booker = new User();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> bookingService.getBookingByUserId(bookerId, stateParam, from, size));

        assertEquals("Unknown state: " + stateParam, ex.getMessage());
    }

    @Test
    void getBookingByOwnerId_whenStateAll_thenReturnBookingList() {
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
    void getBookingByOwnerId_whenStateCurrent_thenReturnBookingList() {
        long ownerId = 1L;
        String stateParam = "current";
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
        when(bookingRepository.findByItem_Owner_IdAndStartLessThanEqualAndEndAfter(anyLong(), any(LocalDateTime.class),
            any(LocalDateTime.class), any(PageRequest.class))).thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByOwnerId(ownerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByOwnerId_whenStatePast_thenReturnBookingList() {
        long ownerId = 1L;
        String stateParam = "past";
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
        when(bookingRepository.findByItem_Owner_IdAndEndBefore(anyLong(), any(LocalDateTime.class),
            any(PageRequest.class))).thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByOwnerId(ownerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByOwnerId_whenStateFuture_thenReturnBookingList() {
        long ownerId = 1L;
        String stateParam = "future";
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
        when(bookingRepository.findByItem_Owner_IdAndStartAfter(anyLong(), any(LocalDateTime.class),
            any(PageRequest.class))).thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByOwnerId(ownerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByOwnerId_whenStateWaiting_thenReturnBookingLits() {
        long ownerId = 1L;
        String stateParam = "waiting";
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
        when(bookingRepository.findByItem_Owner_IdAndStatusEquals(anyLong(), any(BookingStatus.class),
            any(PageRequest.class))).thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByOwnerId(ownerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByOwnerId_whenStateRejected_thenReturnBookingList() {
        long ownerId = 1L;
        String stateParam = "rejected";
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
        when(bookingRepository.findByItem_Owner_IdAndStatusEquals(anyLong(), any(BookingStatus.class),
            any(PageRequest.class))).thenReturn(bookingPage);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingService
            .getBookingByOwnerId(ownerId, stateParam, from, size);

        assertEquals(bookingInfoDtoList, resultBookingInfoDtoList);
    }

    @Test
    void getBookingByOwnerId_whenStateUnknown_thenThrowIllegalStateException() {
        long ownerId = 1L;
        String stateParam = "unknown";
        int from = 2;
        int size = 5;

        User owner = new User();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> bookingService.getBookingByOwnerId(ownerId, stateParam, from, size));

        assertEquals("Unknown state: " + stateParam, ex.getMessage());
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
    void createBooking_whenBookerIsOwner_thenReturnException() {
        long bookerId = 1;

        User booker = new User();
        booker.setId(bookerId);

        Item item = new Item();
        item.setAvailable(true);
        User owner = new User();
        owner.setId(1);
        item.setOwner(owner);

        BookingDto bookingDto = new BookingDto();
        Booking booking = BookingMapper.toBooking(bookingDto);

        booking.setBooker(booker);
        booking.setItem(item);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));


        WrongOwnerException ex = assertThrows(WrongOwnerException.class, () -> bookingService
            .createBooking(bookerId, bookingDto));

        assertEquals("Владелец не может быть арендатором", ex.getMessage());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createBooking_whenBookingItemAvailableFalse_thenReturnException() {
        long bookerId = 1;

        User booker = new User();
        booker.setId(bookerId);

        Item item = new Item();
        item.setAvailable(false);
        User owner = new User();
        owner.setId(2);
        item.setOwner(owner);

        BookingDto bookingDto = new BookingDto();
        Booking booking = BookingMapper.toBooking(bookingDto);

        booking.setBooker(booker);
        booking.setItem(item);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException ex = assertThrows(ValidationException.class,
            () -> bookingService.createBooking(bookerId, bookingDto));

        assertEquals("Товар с id: " + item.getId() + " недоступен для аренды", ex.getMessage());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void approveBooking_whenBookingStatusWaiting_thenReturnApprovedBooking() {
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

    @Test
    void approveBooking_whenBookingStatusApproved_thenReturnException() {
        long ownerId = 1;
        long bookingId = 1;

        Item item = new Item();
        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);

        Booking booking = new Booking();

        booking.setItem(item);
        booking.setBooker(new User());
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        ValidationException ex = assertThrows(ValidationException.class,
            () -> bookingService.approveBooking(ownerId, bookingId, true));

        assertEquals("Аренда уже подтверждена", ex.getMessage());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void approveBooking_whenNotOwnerTryChangeApprove_thenReturnException() {
        long userId = 1;
        long bookingId = 1;

        Item item = new Item();
        User owner = new User();
        owner.setId(2);
        item.setOwner(owner);

        Booking booking = new Booking();

        booking.setItem(item);
        booking.setBooker(new User());
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        WrongOwnerException ex = assertThrows(WrongOwnerException.class,
            () -> bookingService.approveBooking(userId, bookingId, true));

        assertEquals("Только владелец может менять статус аренды с id: " + bookingId, ex.getMessage());
        verify(bookingRepository, never()).save(booking);
    }
}