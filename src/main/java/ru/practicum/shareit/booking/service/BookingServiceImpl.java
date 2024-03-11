package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingInfoDto getBookingById(long bookingId, long userId) {
        validUser(userId);
        Booking booking = validBooking(bookingId);

        if (booking.getBooker().getId() != userId &&
            booking.getItem().getOwner().getId() != userId) {
            throw new WrongOwnerException("Доступно только для владельца вещи или автора аренды");
        }

        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public List<BookingInfoDto> getBookingByUserId(long bookerId, String stateParameter, Integer from, Integer size) {
        validUser(bookerId);
        validPagination(from, size);
        List<Booking> bookings;
        BookingState state = BookingState.checkState(stateParameter);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_Id(bookerId, pageRequest).getContent();
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartAfter(bookerId, now, pageRequest).getContent();
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndBefore(bookerId, now, pageRequest).getContent();
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartLessThanEqualAndEndAfter(bookerId, now, now, pageRequest).getContent();
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusEquals(bookerId, BookingStatus.WAITING, pageRequest).getContent();
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusEquals(bookerId, BookingStatus.REJECTED, pageRequest).getContent();
                break;
            default:
                throw new IllegalArgumentException("Неизвестный статус: " + stateParameter);
        }

        return bookings.stream()
            .map(BookingMapper::toBookingInfoDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingInfoDto> getBookingByOwnerId(long ownerId, String stateParameter, Integer from, Integer size) {
        validUser(ownerId);
        validPagination(from, size);
        List<Booking> bookings;
        BookingState state = BookingState.checkState(stateParameter);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItem_Owner_Id(ownerId, pageRequest).getContent();
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfter(ownerId, now, pageRequest).getContent();
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndBefore(ownerId, now, pageRequest).getContent();
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartLessThanEqualAndEndAfter(ownerId, now, now, pageRequest).getContent();
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusEquals(ownerId, BookingStatus.WAITING, pageRequest).getContent();
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusEquals(ownerId, BookingStatus.REJECTED, pageRequest).getContent();
                break;
            default:
                throw new IllegalArgumentException("Неизвестный статус: " + stateParameter);
        }

        return bookings.stream()
            .map(BookingMapper::toBookingInfoDto)
            .collect(Collectors.toList());
    }

    @Override
    public BookingInfoDto createBooking(long userId, BookingDto bookingDto) {
        log.info("Создать аренду от пользователя с id= {}", userId);
        User booker = validUser(userId);
        Item item = validItem(bookingDto.getItemId());
        User owner = item.getOwner();

        if (booker.getId() == owner.getId()) {
            throw new WrongOwnerException("Владелец не может быть арендатором");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Товар с id: " + item.getId() + " недоступна для аренды");
        }

        validDate(bookingDto);

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        Booking savedBooking = bookingRepository.save(booking);

        log.info("Аренда пользователя с id = {} успешно создана", userId);

        return BookingMapper.toBookingInfoDto(savedBooking);
    }

    @Override
    public BookingInfoDto approveBooking(long ownerId, long bookingId, Boolean approved) {
        Booking booking = validBooking(bookingId);
        User owner = validUser(ownerId);

        if (booking.getBooker().getId() == owner.getId()) {
            throw new WrongOwnerException("Только владелец может менять статус аренды с id: " + bookingId);
        }

        if (approved) {
            if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                throw new ValidationException("Аренда уже подтверждена");
            }
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingInfoDto(savedBooking);
    }

    private User validUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Не найден пользователь с id: " + userId);
        }
        return user.get();
    }

    private Booking validBooking(long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new DataNotFoundException("Не найдена аренда с id: " + bookingId);
        }
        return optionalBooking.get();
    }

    private Item validItem(long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new DataNotFoundException("Не найден товар с id: " + itemId);
        }
        return item.get();
    }

    private void validDate(BookingDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime now = LocalDateTime.now();

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Отсутствует или конец аренды");
        }

        if (start.equals(end)) {
            throw new ValidationException("Начало аренды не может совпадать с окончанием");
        }

        if (start.isAfter(end)) {
            throw new ValidationException("Начало аренды не может быть после ее окончания");
        }

        if (start.isBefore(now)) {
            throw new ValidationException("Начало аренды не может быть в прошлом");
        }
    }

    private void validPagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не должны быть отрицательными");
        }
    }
}
