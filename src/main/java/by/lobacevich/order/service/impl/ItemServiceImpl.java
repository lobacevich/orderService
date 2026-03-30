package by.lobacevich.order.service.impl;

import by.lobacevich.order.dto.request.ItemDtoRequest;
import by.lobacevich.order.dto.response.ItemDtoResponse;
import by.lobacevich.order.entity.Item;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.mapper.ItemMapper;
import by.lobacevich.order.repository.ItemRepository;
import by.lobacevich.order.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final ItemMapper mapper;

    @Override
    public ItemDtoResponse create(ItemDtoRequest dtoRequest) {
        return mapper.entityToDto(repository.save(mapper.dtoToEntity(dtoRequest)));
    }

    @Transactional
    @Override
    public ItemDtoResponse update(ItemDtoRequest dtoRequest, Long id) {
        Item oldItem = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Item not found with id " + id));
        oldItem.setName(dtoRequest.name());
        oldItem.setPrice(dtoRequest.price());
        return mapper.entityToDto(repository.save(oldItem));
    }

    @Override
    public ItemDtoResponse getById(Long id) {
        return mapper.entityToDto(repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Item not found with id " + id)));
    }

    @Override
    public Page<ItemDtoResponse> getAll(int number,
                                        int size) {
        Pageable pageable = PageRequest.of(number, size);
        return repository.findAll(pageable).map(mapper::entityToDto);
    }
}
