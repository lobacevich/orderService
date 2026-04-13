package by.lobacevich.order.service;

import by.lobacevich.order.dto.request.OrderItemDtoRequest;
import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemService {
    List<OrderItem> buildOrderItems(List<OrderItemDtoRequest> dtoRequestList, Order order);

    BigDecimal calculateTotalPrice(List<OrderItem> orderItems);
}
