package by.lobacevich.order.service.impl;

import by.lobacevich.order.dto.request.OrderItemDtoRequest;
import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.OrderItem;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.repository.ItemRepository;
import by.lobacevich.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final ItemRepository itemRepository;

    @Override
    public List<OrderItem> buildOrderItems(List<OrderItemDtoRequest> dtoRequestList, Order order) {
        return dtoRequestList.stream()
                .map(orderItemDto -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setQuantity(orderItemDto.quantity());
                    orderItem.setItem(itemRepository.findById(orderItemDto.itemId()).orElseThrow(() ->
                            new EntityNotFoundException("Item not found with id " + orderItemDto.itemId())));
                    return orderItem;
                })
                .toList();
    }

    @Override
    public BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> orderItem.getItem().getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
