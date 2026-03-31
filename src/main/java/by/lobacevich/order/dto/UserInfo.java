package by.lobacevich.order.dto;

public record UserInfo(Long id,
                       String name,
                       String surname,
                       String birthDate,
                       String email,
                       Boolean active,
                       String createdAt,
                       String updatedAt) {
}
