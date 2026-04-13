package by.lobacevich.order.service.impl;

import by.lobacevich.order.dto.request.OrderDtoRequest;
import by.lobacevich.order.dto.request.StatusDtoRequest;
import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.OrderItem;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.repository.OrderRepository;
import by.lobacevich.order.security.SecurityUtils;
import by.lobacevich.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderTxService {

    private static final String MESSAGE_NOT_FOUND = "Order not found for id ";

    private final OrderRepository repository;
    private final OrderItemService orderItemService;

    @Transactional
    public Order createOrderTx(OrderDtoRequest dtoRequest) {
        Order order = new Order();
        order.setUserId(SecurityUtils.getCurrentUserId());
        List<OrderItem> orderItems = orderItemService.buildOrderItems(dtoRequest.orderItems(), order);
        order.setOrderItems(orderItems);
        order.setTotalPrice(orderItemService.calculateTotalPrice(orderItems));
        return repository.save(order);
    }

    @Transactional
    public Order updateOrderTx(OrderDtoRequest dtoRequest, Long id) {
        Order order = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MESSAGE_NOT_FOUND + id));
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.clear();
        orderItems.addAll(orderItemService.buildOrderItems(dtoRequest.orderItems(), order));
        order.setTotalPrice(orderItemService.calculateTotalPrice(orderItems));
        return repository.save(order);
    }

    @Transactional
    public Order setStatusTx(StatusDtoRequest statusDtoRequest, Long id) {
        Order order = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MESSAGE_NOT_FOUND + id));
        order.setStatus(statusDtoRequest.status());
        return repository.save(order);
    }
}
