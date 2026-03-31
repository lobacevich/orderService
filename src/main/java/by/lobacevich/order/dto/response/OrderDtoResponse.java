package by.lobacevich.order.dto.response;

import by.lobacevich.order.dto.UserInfo;

public record OrderDtoResponse(Long id,
                               Long userId,
                               String status,
                               double totalPrice,
                               boolean deleted,
                               String createdAt,
                               String updatedAt,
                               UserInfo userInfo) {
}
