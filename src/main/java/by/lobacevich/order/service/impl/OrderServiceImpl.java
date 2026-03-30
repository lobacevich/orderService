package by.lobacevich.order.service.impl;

import by.lobacevich.order.dto.request.OrderCreateDtoRequest;
import by.lobacevich.order.dto.request.OrderUpdateDtoRequest;
import by.lobacevich.order.dto.request.StatusDtoRequest;
import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.OrderItem;
import by.lobacevich.order.entity.enums.OrderStatus;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.mapper.OrderMapper;
import by.lobacevich.order.repository.OrderRepository;
import by.lobacevich.order.service.OrderItemService;
import by.lobacevich.order.service.OrderService;
import by.lobacevich.order.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderItemService orderItemService;

    @Transactional
    @Override
    public OrderDtoResponseFull create(OrderCreateDtoRequest dtoRequest) {
        Order order = new Order();
        order.setUserId(dtoRequest.userId());
        List<OrderItem> orderItems = orderItemService.buildOrderItems(dtoRequest.orderItems(), order);
        order.setOrderItems(orderItems);
        order.setTotalPrice(orderItemService.calculateTotalPrice(orderItems));
        return mapper.entityToDtoFull(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDtoResponseFull update(OrderUpdateDtoRequest dtoRequest, Long id) {
        Order order = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Order not found with id " + id));
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.clear();
        orderItems.addAll(orderItemService.buildOrderItems(dtoRequest.orderItems(), order));
        order.setTotalPrice(orderItemService.calculateTotalPrice(orderItems));
        return mapper.entityToDtoFull(repository.save(order));
    }

    @Override
    public OrderDtoResponseFull getById(Long id) {
        return mapper.entityToDtoFull(repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Order not found with id " + id)));
    }

    @Override
    public Page<OrderDtoResponse> getAll(Boolean deleted,
                                         List<OrderStatus> statuses,
                                         LocalDateTime from,
                                         LocalDateTime to,
                                         int number,
                                         int size) {
        Pageable pageable = PageRequest.of(number, size);
        Specification<Order> spec = OrderSpecification.filterBy(deleted, statuses, from, to);
        return repository.findAll(spec, pageable).map(mapper::entityToDto);
    }

    @Override
    public List<OrderDtoResponse> getByUserId(Long id) {
        return repository.findByUserId(id).stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Transactional
    @Override
    public OrderDtoResponseFull setStatus(StatusDtoRequest statusDtoRequest, Long id) {
        Order order = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Order not found with id " + id));
        order.setStatus(statusDtoRequest.status());
        return mapper.entityToDtoFull(repository.save(order));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (repository.delete(id) == 0) {
            throw new EntityNotFoundException("Order not found with id " + id);
        }
    }
}
