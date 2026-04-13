package by.lobacevich.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDtoRequest(@NotNull(message = "Item id is required")
                                  @Min(value = 1, message = "Item id must be greater than 0")
                                  Long itemId,

                                  @NotNull(message = "Quantity id is required")
                                  @Min(value = 1, message = "Quantity must be greater than 0")
                                  int quantity) {
}
