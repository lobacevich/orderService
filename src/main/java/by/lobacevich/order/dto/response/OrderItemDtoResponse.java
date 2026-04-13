package by.lobacevich.order.dto.response;

public record OrderItemDtoResponse(Long id,
                                   ItemDtoResponse item,
                                   int quantity) {
}
