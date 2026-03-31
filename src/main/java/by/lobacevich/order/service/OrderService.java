package by.lobacevich.order.service;

import by.lobacevich.order.dto.request.OrderCreateDtoRequest;
import by.lobacevich.order.dto.request.OrderUpdateDtoRequest;
import by.lobacevich.order.dto.request.StatusDtoRequest;
import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderDtoResponseFull create(OrderCreateDtoRequest dtoRequest);

    OrderDtoResponseFull update(OrderUpdateDtoRequest dtoRequest, Long id);

    OrderDtoResponseFull getById(Long id);

    Page<OrderDtoResponse> getAll(Boolean deleted,
                                  List<OrderStatus> statuses,
                                  LocalDate from,
                                  LocalDate to,
                                  int number,
                                  int size);

    List<OrderDtoResponse> getByUserId(Long id);

    OrderDtoResponseFull setStatus(StatusDtoRequest statusDtoRequest, Long id);

    void delete(Long id);
}
