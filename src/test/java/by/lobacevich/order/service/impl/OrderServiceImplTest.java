package by.lobacevich.order.service.impl;

import by.lobacevich.order.client.UserClient;
import by.lobacevich.order.dto.request.OrderDtoRequest;
import by.lobacevich.order.dto.request.StatusDtoRequest;
import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.dto.response.UserInfo;
import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.enums.OrderStatus;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.mapper.OrderMapper;
import by.lobacevich.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    public static final Long ID = 1L;
    public static final int NUMBER = 0;
    public static final int SIZE = 2;

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private OrderTxService orderTxService;

    @Mock
    private UserClient userClient;

    @Mock
    private OrderDtoRequest dtoRequest;

    @Mock
    private OrderDtoResponseFull dtoResponseFull;

    @Mock
    private OrderDtoResponse dtoResponse;

    @Mock
    private Order order;

    @Mock
    private UserInfo userInfo;

    @InjectMocks
    private OrderServiceImpl service;

    @Test
    void create_ShouldCallSaveMethodOfRepositoryAndReturnOrderDtoResponseFull() {
        when(orderTxService.createOrderTx(dtoRequest)).thenReturn(order);
        when(order.getUserId()).thenReturn(ID);
        when(userClient.getUserById(ID)).thenReturn(userInfo);
        when(mapper.entityToDtoFull(order, userInfo)).thenReturn(dtoResponseFull);

        OrderDtoResponseFull actual = service.create(dtoRequest);

        assertEquals(dtoResponseFull, actual);

    }

    @Test
    void update_ShouldReturnOrderDtoResponseFull() {
        when(orderTxService.updateOrderTx(dtoRequest, ID)).thenReturn(order);
        when(order.getUserId()).thenReturn(ID);
        when(userClient.getUserById(ID)).thenReturn(userInfo);
        when(mapper.entityToDtoFull(order, userInfo)).thenReturn(dtoResponseFull);

        OrderDtoResponseFull actual = service.update(dtoRequest, ID);

        assertEquals(dtoResponseFull, actual);
    }

    @Test
    void getById_ShouldReturnOrderDtoResponseFull() {
        when(repository.findById(ID)).thenReturn(Optional.of(order));
        when(order.getUserId()).thenReturn(ID);
        when(userClient.getUserById(ID)).thenReturn(userInfo);
        when(mapper.entityToDtoFull(order, userInfo)).thenReturn(dtoResponseFull);

        OrderDtoResponseFull actual = service.getById(ID);

        assertEquals(dtoResponseFull, actual);
    }

    @Test
    void getById_ShouldThrowEntityNotFoundException() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(ID));
    }

    @Test
    void getAll_ShouldReturnPageOfOrderDtoResponse() {
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(order)));
        when(userClient.getUsersByIds(any(List.class))).thenReturn(Collections.emptyList());
        when(mapper.entityToDto(order, null)).thenReturn(dtoResponse);

        Page<OrderDtoResponse> actual = service.getAll(null, null, null, null, NUMBER, SIZE);

        assertEquals(new PageImpl<>(List.of(dtoResponse)), actual);
    }

    @Test
    void getByUserId_ShouldReturnListOfOrderDtoResponse() {
        when(repository.findByUserId(ID)).thenReturn(List.of(order));

        when(mapper.entityToDto(order, null)).thenReturn(dtoResponse);
        when(userClient.getUserById(ID)).thenReturn(null);
        List<OrderDtoResponse> actual = service.getByUserId(ID);

        assertEquals(List.of(dtoResponse), actual);
    }

    @Test
    void setStatus_ShouldSaveOrderWithNewStatusAndReturnOrderDtoResponseFull() {
        StatusDtoRequest statusDtoRequest = new StatusDtoRequest(OrderStatus.PAID);
        when(orderTxService.setStatusTx(statusDtoRequest, ID)).thenReturn(order);
        when(order.getUserId()).thenReturn(ID);
        when(userClient.getUserById(ID)).thenReturn(userInfo);
        when(mapper.entityToDtoFull(order, userInfo)).thenReturn(dtoResponseFull);

        OrderDtoResponseFull actual = service.setStatus(statusDtoRequest, ID);

        assertEquals(dtoResponseFull, actual);
    }

    @Test
    void delete_ShouldCallRepositoryMethodDelete() {
        when(repository.delete(ID)).thenReturn(1);

        service.delete(ID);

        verify(repository, times(1)).delete(ID);
    }

    @Test
    void delete_ShouldThrowEntityNotFoundException() {
        when(repository.delete(ID)).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> service.delete(ID));
    }
}