package by.lobacevich.order.service.impl;

import by.lobacevich.order.dto.request.OrderItemDtoRequest;
import by.lobacevich.order.entity.Item;
import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.OrderItem;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    public static final Long ID = 1L;
    public static final BigDecimal PRICE1 = BigDecimal.valueOf(13.21);
    public static final BigDecimal PRICE2 = BigDecimal.valueOf(26.47);
    public static final int QUANTITY1 = 2;
    public static final int QUANTITY2 = 3;
    public static final BigDecimal RESULT = BigDecimal.valueOf(105.83);

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private Order order;

    @Mock
    private Item item;

    @Mock
    private Item otherItem;

    @Mock
    private OrderItem orderItem;

    @Mock
    private OrderItem otherOrderItem;

    @Mock
    private OrderItemDtoRequest dtoRequest;

    @InjectMocks
    private OrderItemServiceImpl service;

    @Test
    void buildOrderItems_ShouldReturnListOfOrderItem() {
        when(dtoRequest.itemId()).thenReturn(ID);
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));

        List<OrderItem> actual = service.buildOrderItems(List.of(dtoRequest), order);

        assertEquals(1, actual.size());
        assertEquals(order, actual.getFirst().getOrder());
        assertEquals(item, actual.getFirst().getItem());
    }

    @Test
    void buildOrderItems_ShouldThrowEntityNotFoundException() {
        when(dtoRequest.itemId()).thenReturn(ID);
        when(itemRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buildOrderItems(List.of(dtoRequest), order));
    }

    @Test
    void calculateTotalPrice_ShouldReturnResult() {
        when(orderItem.getItem()).thenReturn(item);
        when(otherOrderItem.getItem()).thenReturn(otherItem);
        when(item.getPrice()).thenReturn(PRICE1);
        when(otherItem.getPrice()).thenReturn(PRICE2);
        when(orderItem.getQuantity()).thenReturn(QUANTITY1);
        when(otherOrderItem.getQuantity()).thenReturn(QUANTITY2);

        BigDecimal actual = service.calculateTotalPrice(List.of(orderItem, otherOrderItem));

        assertEquals(RESULT, actual);
    }

    @Test
    void calculateTotalPrice_ShouldReturnZero() {
        BigDecimal actual = service.calculateTotalPrice(List.of());

        assertEquals(BigDecimal.ZERO, actual);
    }
}