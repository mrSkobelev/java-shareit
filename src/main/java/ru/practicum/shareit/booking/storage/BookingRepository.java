package ru.practicum.shareit.booking.storage;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByBooker_Id(long bookerId, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByBooker_IdAndStartAfter(long bookerId, LocalDateTime start, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByBooker_IdAndEndBefore(long bookerId, LocalDateTime end, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByBooker_IdAndStartLessThanEqualAndEndAfter(long bookerId, LocalDateTime start, LocalDateTime end,
        Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByBooker_IdAndStatusEquals(long bookerId, BookingStatus status, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByItem_Owner_Id(long ownerId, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByItem_Owner_IdAndStartAfter(long ownerId, LocalDateTime start, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByItem_Owner_IdAndEndBefore(long ownerId, LocalDateTime end, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByItem_Owner_IdAndStartLessThanEqualAndEndAfter(long ownerId, LocalDateTime start,
        LocalDateTime end, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByItem_Owner_IdAndStatusEquals(long ownerId, BookingStatus status, Sort sort);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByItem_IdAndStatusNot(long itemId, BookingStatus status);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByBooker_IdAndItem_Id_AndEndBefore(long bookerId, long itemId, LocalDateTime localDateTime);

    @EntityGraph(value = "Booking.UserAndItem")
    List<Booking> findByItemInAndStatusNot(List<Item> items, BookingStatus status);
}
