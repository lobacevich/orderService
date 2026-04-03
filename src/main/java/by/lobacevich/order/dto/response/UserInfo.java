package by.lobacevich.order.dto.response;

public record UserInfo(Long id,
                       String name,
                       String surname,
                       String birthDate,
                       String email,
                       Boolean active,
                       String createdAt,
                       String updatedAt) {
}
