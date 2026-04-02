package by.lobacevich.order.service.impl;

import by.lobacevich.order.dto.request.ItemDtoRequest;
import by.lobacevich.order.dto.response.ItemDtoResponse;
import by.lobacevich.order.entity.Item;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.mapper.ItemMapper;
import by.lobacevich.order.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    public static final Long ID = 1L;
    public static final int NUMBER = 0;
    public static final int SIZE = 2;

    @Mock
    private ItemRepository repository;

    @Mock
    private ItemMapper mapper;

    @Mock
    private Item item;

    @Mock
    private ItemDtoResponse dtoResponse;

    @Mock
    private ItemDtoRequest dtoRequest;

    @InjectMocks
    private ItemServiceImpl service;

    @Test
    void create_ShouldCallSaveMethodOfRepositoryAndReturnItemDtoResponse() {
        when(mapper.dtoToEntity(dtoRequest)).thenReturn(item);
        when(repository.save(item)).thenReturn(item);
        when(mapper.entityToDto(item)).thenReturn(dtoResponse);

        ItemDtoResponse actual = service.create(dtoRequest);

        verify(repository, times(1)).save(item);
        assertEquals(dtoResponse, actual);
    }

    @Test
    void getById_ShouldReturnItemDtoResponse() {
        when(repository.findById(ID)).thenReturn(Optional.of(item));
        when(mapper.entityToDto(item)).thenReturn(dtoResponse);

        ItemDtoResponse actual = service.getById(ID);

        assertEquals(dtoResponse, actual);
    }

    @Test
    void getById_ShouldThrowEntityNotFoundException() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(ID));
    }

    @Test
    void getAll_ShouldReturnPageOfItemDtoResponse() {
        when(repository.findAll(PageRequest.of(NUMBER, SIZE))).thenReturn(new PageImpl<>(List.of(item)));
        when(mapper.entityToDto(item)).thenReturn(dtoResponse);

        Page<ItemDtoResponse> actual = service.getAll(NUMBER, SIZE);

        assertEquals(new PageImpl<>(List.of(dtoResponse)), actual);
    }
}
