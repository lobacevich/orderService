package by.lobacevich.order.dto.response;

public record ItemDtoResponse(Long id,
                              String name,
                              double price,
                              String createdAt,
                              String updatedAt) {
}
