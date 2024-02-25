package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();

        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);

        return item;
    }

    public static ItemInfoDto toItemInfoDto(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemInfoDto itemInfoDto = new ItemInfoDto();

        itemInfoDto.setId(item.getId());
        itemInfoDto.setName(item.getName());
        itemInfoDto.setDescription(item.getDescription());
        itemInfoDto.setAvailable(item.getAvailable());

        List<CommentInfoDto> commentInfoDtoList = comments.stream()
            .map(CommentMapper::toCommentInfoDto)
            .collect(Collectors.toList());
        itemInfoDto.setComments(commentInfoDtoList);

        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookings.stream()
            .filter(booking -> !booking.getStart().isAfter(now))
            .max(Comparator.comparing(Booking::getStart))
            .orElse(null);
        Booking nextBooking = bookings.stream()
            .filter(booking -> booking.getStart().isAfter(now))
            .min(Comparator.comparing(Booking::getStart))
            .orElse(null);

        if (lastBooking != null) {
            itemInfoDto.setLastBooking(new ItemInfoDto.BookingInfoDto(lastBooking.getId(), lastBooking.getBooker().getId()));
        }
        if (nextBooking != null) {
            itemInfoDto.setNextBooking(new ItemInfoDto.BookingInfoDto(nextBooking.getId(), nextBooking.getBooker().getId()));
        }

        return itemInfoDto;
    }
}
