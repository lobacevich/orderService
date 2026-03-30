package by.lobacevich.order.dto.response;

public record OrderDtoResponse(Long id,
                               Long userId,
                               String status,
                               double totalPrice,
                               boolean deleted,
                               String createdAt,
                               String updatedAt) {
}
