package ru.practicum.shareit.item.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemInfoDto getItemById(long userId, long itemId) {
        log.info("Получить товар с id = {}", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new DataNotFoundException(""));

        List<Comment> comments = commentRepository.findByItem_Id(itemId);

        if (userId == item.getOwner().getId()) {
            List<Booking> bookings = bookingRepository.findByItem_IdAndStatusNot(itemId, BookingStatus.REJECTED);
            log.debug("Выгружена вещь с id = {}", itemId);

            return ItemMapper.toItemInfoDto(item, bookings, comments);
        }
        log.debug("Выгружена вещь с id = {}", itemId);

        return ItemMapper.toItemInfoDto(item, new ArrayList<>(), comments);
    }

    /*
    @Override
    public List<ItemInfoDto> getAllItems() {
        log.info("Получить все товары");

        List<Item> items = itemStorage.findAll();
        List<ItemDto> itemDtoList = new ArrayList<>();

        if (!items.isEmpty()) {
            for (Item i : items) {
                itemDtoList.add(ItemMapper.toItemDto(i));
            }
        }
        return itemDtoList;
    }

     */

    @Override
    public List<ItemInfoDto> getAllItemsByUserId(long userId) {
        log.info("Получить все товары пользователя с id = {}", userId);

        List<Item> items = itemRepository.findByOwnerId(userId);

        Map<Item, List<Comment>> itemCommentsMap = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
            .stream()
            .collect(groupingBy(Comment::getItem, toList()));

        Map<Item, List<Booking>> itemBookingsMap = bookingRepository.findByItemInAndStatusNot(items, BookingStatus.REJECTED)
            .stream()
            .collect(groupingBy(Booking::getItem, toList()));

        log.debug("Выгружен список товаров пользователя с id = {}", userId);

        return items.stream()
            .map(item -> ItemMapper.toItemInfoDto(item,
                itemBookingsMap.getOrDefault(item, Collections.emptyList()),
                itemCommentsMap.getOrDefault(item, Collections.emptyList())))
            .sorted(Comparator.comparing(ItemInfoDto::getId))
            .collect(toList());
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        log.info("Создать товар пользователя с id = {}", userId);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Не найден пользователь с id: " + userId);
        }

        Item item = ItemMapper.toItem(itemDto, optionalUser.get());
        Item savedItem = itemRepository.save(item);

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        log.info("Обновить товар с id = {}", itemId);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Не найден пользователь с id: " + userId);
        }

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new DataNotFoundException("Не найден товар с id: " + itemId);
        }

        Item item = optionalItem.get();
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Поиск товара по значению {}", text.toUpperCase());

        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        List<Item> items = itemRepository.searchItemByText(text);
        if (!items.isEmpty()) {
            for (Item i : items) {
                itemDtoList.add(ItemMapper.toItemDto(i));
            }
        }

        return itemDtoList;
    }

    @Override
    public CommentInfoDto addComment(long userId, long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findByBooker_IdAndItem_Id_AndEndBefore(
            userId, itemId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new ValidationException("Только арендатор может оставлять отзыв");
        }

        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Отсутствует текст комментария");
        }

        User author = validUser(userId);
        Item item = validItem(itemId);
        Comment comment = CommentMapper.toComment(commentDto, author, item);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentInfoDto(savedComment);
    }

    private User validUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Не найден пользователь с id: " + userId);
        }
        return user.get();
    }

    private Booking validBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new DataNotFoundException("Не найдена аренда с id: " + bookingId);
        }
        return optionalBooking.get();
    }

    private Item validItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new DataNotFoundException("Не найден товар с id: " + itemId);
        }
        return item.get();
    }
}