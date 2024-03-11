package ru.practicum.shareit.booking.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@DataJpaTest
class BookingRepositoryIT {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestsRepository itemRequestRepository;
    @Autowired
    BookingRepository bookingRepository;
    User owner;
    User requester;
    User booker;
    ItemRequest itemRequest;
    Item item;
    Booking booking;
    List<Item> items;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.com");
        userRepository.save(owner);

        requester = new User();
        requester.setName("requester");
        requester.setEmail("requester@mail.com");
        userRepository.save(requester);

        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.com");
        userRepository.save(booker);

        itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("requestDescription");
        itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("item");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        items = List.of(item);
        itemRepository.save(item);

        booking = new Booking();
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusSeconds(1));
        booking.setEnd(LocalDateTime.now().plusSeconds(60));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        bookingRepository.save(booking);
    }

    @Test
    void findByBooker_Id_whenBookingCreated_thenReturnList() {
        List<Booking> bookings = bookingRepository.findByBooker_Id(booker.getId(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByBooker_IdAndStartAfter_whenStartAfterNow_thenReturnList() {
        List<Booking> bookings = bookingRepository
            .findByBooker_IdAndStartAfter(booker.getId(), LocalDateTime.now(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByBooker_IdAndEndBefore_whenEndBeforeNow_thenReturnList() {
        booking.setStart(LocalDateTime.now().minusSeconds(3));
        booking.setEnd(LocalDateTime.now().minusSeconds(20));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
            .findByBooker_IdAndEndBefore(booker.getId(), LocalDateTime.now(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByBooker_IdAndStartLessThanEqualAndEndAfter_whenStartBeforeNowAndEndBeforeNow_thenReturnList() {
        booking.setStart(LocalDateTime.now().minusSeconds(3));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
            .findByBooker_IdAndStartLessThanEqualAndEndAfter(booker.getId(), LocalDateTime.now(),
                LocalDateTime.now(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByBooker_IdAndStatusEquals_whenStatusApproved_thenReturnList() {
        List<Booking> bookings = bookingRepository
            .findByBooker_IdAndStatusEquals(booker.getId(), BookingStatus.APPROVED, PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItem_Owner_Id_whenItemOwnerIdIsPresent_thenReturnList() {
        List<Booking> bookings = bookingRepository.findByItem_Owner_Id(owner.getId(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItem_Owner_IdAndStartAfter_whenStartAfterNow_thenReturnList() {
        List<Booking> bookings = bookingRepository
            .findByItem_Owner_IdAndStartAfter(owner.getId(), LocalDateTime.now(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItem_Owner_IdAndEndBefore_whenEndBeforeNow_thenReturnList() {
        booking.setStart(LocalDateTime.now().minusSeconds(3));
        booking.setEnd(LocalDateTime.now().minusSeconds(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
            .findByItem_Owner_IdAndEndBefore(owner.getId(), LocalDateTime.now(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItem_Owner_IdAndStartLessThanEqualAndEndAfter_whenStartBeforeNowAndEndAfterNow_thenReturnList() {
        booking.setStart(LocalDateTime.now().minusSeconds(3));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
            .findByItem_Owner_IdAndStartLessThanEqualAndEndAfter(owner.getId(), LocalDateTime.now(),
                LocalDateTime.now(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItem_Owner_IdAndStatusEquals_whenStatusApproved_thenReturnList() {
        List<Booking> bookings = bookingRepository
            .findByItem_Owner_IdAndStatusEquals(owner.getId(), BookingStatus.APPROVED, PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItem_IdAndStatusNot_whenStatusRejected_thenReturnList() {
        List<Booking> bookings = bookingRepository.findByItem_IdAndStatusNot(item.getId(), BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByBooker_IdAndItem_Id_AndEndBefore_whenEndBeforeNow_thenReturnList() {
        booking.setStart(LocalDateTime.now().minusSeconds(10));
        booking.setEnd(LocalDateTime.now().minusSeconds(5));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
            .findByBooker_IdAndItem_Id_AndEndBefore(booker.getId(), item.getId(), LocalDateTime.now());

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemInAndStatusNot_whenStatusRejected_thenReturnList() {
        List<Booking> bookings = bookingRepository.findByItemInAndStatusNot(items, BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
    }

    @AfterEach
    void deleteAllItem() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}