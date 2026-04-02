package by.lobacevich.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderDtoRequest(@NotEmpty(message = "Items in order are required")
                              @Valid
                              List<OrderItemDtoRequest> orderItems) {
}
