package by.lobacevich.order.dto.request;

import by.lobacevich.order.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusDtoRequest(@NotNull(message = "Order status is required")
                               OrderStatus status) {
}
