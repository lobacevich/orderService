package by.lobacevich.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderUpdateDtoRequest(@NotNull(message = "Items in order are required")
                                    @Valid
                                    List<OrderItemDtoRequest> orderItems) {
}
