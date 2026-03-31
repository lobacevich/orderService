package by.lobacevich.order.dto.response;

import by.lobacevich.order.dto.UserInfo;

import java.util.List;

public record OrderDtoResponseFull(Long id,
                                   Long userId,
                                   String status,
                                   double totalPrice,
                                   boolean deleted,
                                   String createdAt,
                                   String updatedAt,
                                   UserInfo userInfo,
                                   List<OrderItemDtoResponse> orderItems) {
}
