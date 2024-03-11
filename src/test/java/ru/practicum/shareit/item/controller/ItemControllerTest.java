package ru.practicum.shareit.item.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    @Test
    void getItemById_whenItemCreated_thenReturnItem() {
        long userId = 1L;
        long itemId = 1L;

        Item item = new Item();;
        item.setId(itemId);
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);
        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item, new ArrayList<>(), new ArrayList<>());

        when(itemService.getItemById(userId, itemId)).thenReturn(itemInfoDto);

        ItemInfoDto actualItemInfoDto = itemController.getItemById(userId, itemId);

        assertEquals(itemInfoDto, actualItemInfoDto);
    }

    @Test
    void getAllItemsByUserId_whenItemsCreated_thenReturnListAndEqualsSize() {
        List<ItemInfoDto> itemInfoDtos = List.of(new ItemInfoDto());

        when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(itemInfoDtos);

        List<ItemInfoDto> itemInfoDtoList = itemController.getAllItemsByUserId(anyLong(), anyInt(), anyInt());

        assertEquals(itemInfoDtos.size(), itemInfoDtoList.size());
    }

    @Test
    void createItem_whenItemValid_thenReturnItem() {
        long userId = 1L;

        Item item = new Item();
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.createItem(userId, itemDto)).thenReturn(itemDto);

        ItemDto actualItemDto = itemController.createItem(userId, itemDto);

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void updateItem_whenItemValid_thenReturnItem() {
        long userId = 1L;
        long itemId = 1L;

        Item item = new Item();
        item.setId(itemId);
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.updateItem(itemDto, userId, itemId)).thenReturn(itemDto);

        ItemDto actualItemDto = itemController.updateItem(userId, itemDto, itemId);

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void searchItem_whenTextIsPresent_thenReturnList() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        List<ItemDto> itemDtoList = List.of(itemDto);

        when(itemService.searchItem("name", 0, 1)).thenReturn(itemDtoList);

        List<ItemDto> resultItemDtoList = itemController.searchItem("name", 0, 1);

        assertEquals("name", resultItemDtoList.get(0).getName());
    }

    @Test
    void addComment_whenCommentValid_thenReturnComment() {
        long authorId = 1L;
        long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");

        Comment comment = new Comment();
        User author = new User();
        author.setId(authorId);
        comment.setAuthor(author);
        Item item = new Item();
        item.setId(itemId);
        comment.setItem(item);
        CommentInfoDto commentInfoDto = new CommentInfoDto();
        commentInfoDto.setAuthorName(author.getName());


        when(itemService.addComment(authorId, itemId, commentDto)).thenReturn(commentInfoDto);

        CommentInfoDto actualCommentInfoDto = itemController.addComment(authorId, itemId, commentDto);

        assertEquals(commentInfoDto, actualCommentInfoDto);
    }
}