package by.lobacevich.order.service.impl;

import by.lobacevich.order.client.UserClient;
import by.lobacevich.order.dto.response.UserInfo;
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
import by.lobacevich.order.service.OrderService;
import by.lobacevich.order.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private static final String MESSAGE_NOT_FOUND = "Order not found with id ";

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderItemService orderItemService;
    private final UserClient userClient;

    @Transactional
    @Override
    public OrderDtoResponseFull create(OrderDtoRequest dtoRequest) {
        Order order = new Order();
        order.setUserId(SecurityUtils.getCurrentUserId());
        List<OrderItem> orderItems = orderItemService.buildOrderItems(dtoRequest.orderItems(), order);
        order.setOrderItems(orderItems);
        order.setTotalPrice(orderItemService.calculateTotalPrice(orderItems));
        return mapper.entityToDtoFull(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDtoResponseFull update(OrderDtoRequest dtoRequest, Long id) {
        Order order = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MESSAGE_NOT_FOUND + id));
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.clear();
        orderItems.addAll(orderItemService.buildOrderItems(dtoRequest.orderItems(), order));
        order.setTotalPrice(orderItemService.calculateTotalPrice(orderItems));
        return mapper.entityToDtoFull(repository.save(order));
    }

    @Override
    public OrderDtoResponseFull getById(Long id) {
        return mapper.entityToDtoFull(repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MESSAGE_NOT_FOUND + id)));
    }

    @Override
    public Page<OrderDtoResponse> getAll(Boolean deleted,
                                         List<OrderStatus> statuses,
                                         LocalDate from,
                                         LocalDate to,
                                         int number,
                                         int size) {
        Pageable pageable = PageRequest.of(number, size);
        Specification<Order> spec = OrderSpecification.filterBy(deleted, statuses, from, to);

        Page<Order> orders = repository.findAll(spec, pageable);
        List<Long> userIds = orders.stream()
                .map(Order::getUserId)
                .distinct()
                .toList();
        List<UserInfo> users = userClient.getUsersByIds(userIds);
        Map<Long, UserInfo> userMap = users.stream()
                .collect(Collectors.toMap(UserInfo::id, user -> user));

        List<OrderDtoResponse> orderDtoList = orders.stream()
                .map(order -> mapper.entityToDto(order, userMap.get(order.getUserId())))
                .toList();
        return new PageImpl<>(orderDtoList);
    }

    @Override
    public List<OrderDtoResponse> getByUserId(Long id) {
        UserInfo user = userClient.getUserById(id);
        return repository.findByUserId(id).stream()
                .map(order -> mapper.entityToDto(order, user))
                .toList();
    }

    @Transactional
    @Override
    public OrderDtoResponseFull setStatus(StatusDtoRequest statusDtoRequest, Long id) {
        Order order = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MESSAGE_NOT_FOUND + id));
        order.setStatus(statusDtoRequest.status());
        return mapper.entityToDtoFull(repository.save(order));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (repository.delete(id) == 0) {
            throw new EntityNotFoundException(MESSAGE_NOT_FOUND + id);
        }
    }

    public boolean isOwner(Long id) {
        Order order = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MESSAGE_NOT_FOUND + id));
        return SecurityUtils.getCurrentUserId().equals(order.getUserId());
    }
}
