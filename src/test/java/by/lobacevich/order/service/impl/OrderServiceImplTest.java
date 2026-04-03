package by.lobacevich.order.service.impl;

import by.lobacevich.order.client.UserClient;
import by.lobacevich.order.dto.request.OrderDtoRequest;
import by.lobacevich.order.dto.request.StatusDtoRequest;
import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.OrderItem;
import by.lobacevich.order.entity.enums.OrderStatus;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.mapper.OrderMapper;
import by.lobacevich.order.repository.OrderRepository;
import by.lobacevich.order.security.SecurityUtils;
import by.lobacevich.order.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    public static final Long ID = 1L;
    public static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(5.17);
    public static final int NUMBER = 0;
    public static final int SIZE = 2;

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private UserClient userClient;

    @Mock
    private OrderDtoRequest dtoRequest;

    @Mock
    private OrderDtoResponseFull dtoResponseFull;

    @Mock
    private OrderDtoResponse dtoResponse;

    @Mock
    private OrderItem orderItem;

    @Mock
    private Order order;

    @InjectMocks
    private OrderServiceImpl service;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void create_ShouldCallSaveMethodOfRepositoryAndReturnOrderDtoResponseFull() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(ID);
            when(orderItemService.buildOrderItems(anyList(), any(Order.class))).thenReturn(List.of(orderItem));
            when(orderItemService.calculateTotalPrice(List.of(orderItem))).thenReturn(TOTAL_PRICE);
            when(repository.save(any(Order.class))).thenReturn(order);
            when(mapper.entityToDtoFull(order)).thenReturn(dtoResponseFull);

            service.create(dtoRequest);

            verify(repository, times(1)).save(orderCaptor.capture());

            Order saved = orderCaptor.getValue();

            assertEquals(ID, saved.getUserId());
            assertEquals(List.of(orderItem), saved.getOrderItems());
            assertEquals(TOTAL_PRICE, saved.getTotalPrice());
        }
    }

    @Test
    void update_ShouldReturnOrderDtoResponseFull() {
        List<OrderItem> orderItems = new ArrayList<>();
        Order realOrder = new Order();
        realOrder.setOrderItems(orderItems);

        when(repository.findById(ID)).thenReturn(Optional.of(realOrder));
        when(dtoRequest.orderItems()).thenReturn(List.of());
        when(orderItemService.buildOrderItems(anyList(), any(Order.class))).thenReturn(List.of(orderItem));
        when(orderItemService.calculateTotalPrice(List.of(orderItem))).thenReturn(TOTAL_PRICE);
        when(repository.save(any(Order.class))).thenReturn(order);
        when(mapper.entityToDtoFull(order)).thenReturn(dtoResponseFull);

        service.update(dtoRequest, ID);

        verify(repository, times(1)).save(orderCaptor.capture());

        Order saved = orderCaptor.getValue();

        assertEquals(orderItem, saved.getOrderItems().getFirst());
        assertEquals(TOTAL_PRICE, saved.getTotalPrice());
    }

    @Test
    void update_ShouldThrowEntityNotFoundException() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(dtoRequest, ID));
    }

    @Test
    void getById_ShouldReturnOrderDtoResponseFull() {
        when(repository.findById(ID)).thenReturn(Optional.of(order));
        when(mapper.entityToDtoFull(order)).thenReturn(dtoResponseFull);

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
        when(repository.findById(ID)).thenReturn(Optional.of(new Order()));
        when(repository.save(any(Order.class))).thenReturn(order);
        when(mapper.entityToDtoFull(order)).thenReturn(dtoResponseFull);

        OrderDtoResponseFull actual = service.setStatus(new StatusDtoRequest(OrderStatus.PAID), ID);

        assertEquals(dtoResponseFull, actual);
        verify(repository, times(1)).save(orderCaptor.capture());

        Order saved = orderCaptor.getValue();

        assertEquals(OrderStatus.PAID, saved.getStatus());
    }

    @Test
    void setStatus_ShouldThrowEntityNotFoundException() {
        StatusDtoRequest statusDtoRequest = new StatusDtoRequest(OrderStatus.PAID);

        when(repository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.setStatus(statusDtoRequest, ID));
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