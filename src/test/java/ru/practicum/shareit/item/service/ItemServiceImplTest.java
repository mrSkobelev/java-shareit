package ru.practicum.shareit.item.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
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
import ru.practicum.shareit.request.storage.ItemRequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestsRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void getItemById_whenItemValid_thenReturnItem() {
        long userId = 0L;
        long itemId = 0L;
        Item item = new Item();
        ItemInfoDto itemInfoDto = new ItemInfoDto();
        User owner = new User();
        owner.setId(3L);
        item.setOwner(owner);

        Comment comment = new Comment();
        User author = new User();
        author.setName("author");
        comment.setAuthor(author);
        comment.setItem(item);

        List<Comment> comments = List.of(comment);

        ItemInfoDto expectedItemInfoDto = ItemMapper.toItemInfoDto(item, new ArrayList<>(), comments);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItem_Id(itemId)).thenReturn(comments);

        ItemInfoDto actualItemInfoDto = itemService.getItemById(userId, itemId);

        assertEquals(expectedItemInfoDto, actualItemInfoDto);
    }

    @Test
    void getAllItemsByUserId_whenItemsCreated_thenReturnList() {
        long ownerId = 0L;
        int from = 0;
        int size = 1;

        PageRequest pageRequest = PageRequest.of(from / size, size);
        Item item = new Item();
        List<Item> items = List.of(item);
        Page<Item> itemPages = new PageImpl<>(items, pageRequest, items.size());
        Comment comment = new Comment();
        User author = new User();
        author.setName("author");
        comment.setAuthor(author);
        comment.setItem(item);
        List<Comment> comments = List.of(comment);
        ItemInfoDto itemInfoDtoForMapping = new ItemInfoDto();
        itemInfoDtoForMapping.setId(item.getId());
        itemInfoDtoForMapping.setName(item.getName());
        itemInfoDtoForMapping.setAvailable(item.getAvailable());
        itemInfoDtoForMapping.setDescription(item.getDescription());

        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item, new ArrayList<>(), comments);
        List<ItemInfoDto> itemInfoDtoList = List.of(itemInfoDto);

        when(itemRepository.findByOwnerId(ownerId, pageRequest)).thenReturn(itemPages);
        when(commentRepository.findByItemIn(items, Sort.by(DESC, "created")))
            .thenReturn(comments);
        when(bookingRepository.findByItemInAndStatusNot(items, BookingStatus.REJECTED))
            .thenReturn(new ArrayList<>());

        List<ItemInfoDto> resultItemInfoDtoList = itemService.getAllItemsByUserId(ownerId, from, size);

        assertEquals(itemInfoDtoList, resultItemInfoDtoList);
    }

    @Test
    void createItem_whenItemValid_thenReturnItem() {
        long ownerId = 0L;

        Item item = new Item();
        ItemDto itemDto = new ItemDto();
        User owner = new User();

        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto actualItemDto = itemService.createItem(ownerId, itemDto);

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void updateItem_whenItemValid_thenReturnItem() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User();
        Item item = new Item();
        user.setId(userId);
        item.setId(itemId);
        item.setOwner(user);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto actualItemDto = itemService.updateItem(itemDto, userId, itemId);

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void searchItem_whenItemValid_thenReturnItem() {
        String text = "name";
        int from = 0;
        int size = 1;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Item item = new Item();
        item.setName("name");
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName());
        List<Item> items = List.of(item);
        Page<Item> itemPages = new PageImpl<>(items, pageRequest, items.size());

        when(itemRepository.searchItemByText(text, pageRequest)).thenReturn(itemPages);

        List<ItemDto> itemDtoList = itemService.searchItem(text, from, size);

        assertEquals("name", itemDtoList.get(0).getName());
    }

    @Test
    void addComment_whenTextNotBlanc_thenReturnComment() {
        long authorId = 0L;
        long itemId = 0L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        User author = new User();
        Item item = new Item();
        item.setId(itemId);

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        CommentInfoDto commentInfoDto = new CommentInfoDto();
        commentInfoDto.setText(comment.getText());

        Comment mapperComment = CommentMapper.toComment(commentDto, author, item);
        CommentInfoDto mapperCommentInfoDto = CommentMapper.toCommentInfoDto(mapperComment);

        when(bookingRepository.findByBooker_IdAndItem_Id_AndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
            .thenReturn(bookings);
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.save(mapperComment)).thenReturn(mapperComment);

        CommentInfoDto actualCommentInfoDto = itemService.addComment(authorId, itemId, commentDto);

        assertEquals(mapperCommentInfoDto, actualCommentInfoDto);
    }
}