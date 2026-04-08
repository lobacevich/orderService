package by.lobacevich.order.service.impl;

import by.lobacevich.order.dto.request.OrderItemDtoRequest;
import by.lobacevich.order.entity.Item;
import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.OrderItem;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.repository.ItemRepository;
import by.lobacevich.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final ItemRepository itemRepository;

    @Override
    public List<OrderItem> buildOrderItems(List<OrderItemDtoRequest> dtoRequestList, Order order) {
        List<Long> itemIds = dtoRequestList.stream()
                .map(OrderItemDtoRequest::itemId)
                .toList();

        Map<Long, Item> itemsMap = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        return dtoRequestList.stream()
                .map(orderItemDto -> {
                    Item item = itemsMap.get(orderItemDto.itemId());
                    if (item == null) {
                        throw new EntityNotFoundException("Item not found with id " + orderItemDto.itemId());
                    }
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setQuantity(orderItemDto.quantity());
                    orderItem.setItem(item);
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
