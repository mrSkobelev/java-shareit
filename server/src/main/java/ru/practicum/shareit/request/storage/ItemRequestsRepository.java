package ru.practicum.shareit.request.storage;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestsRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterId(Long requesterId, Sort sort);

    Page<ItemRequest> findByRequesterIdNot(long requesterId, PageRequest pageRequest);
}
