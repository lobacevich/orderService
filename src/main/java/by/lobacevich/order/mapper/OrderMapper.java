package by.lobacevich.order.mapper;

import by.lobacevich.order.client.UserClient;
import by.lobacevich.order.dto.response.UserInfo;
import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper {

    @Autowired //NOSONAR
    private UserClient userClient;

    @Mapping(target = "userInfo", expression = "java(getUserInfo(order.getUserId()))")
    public abstract OrderDtoResponseFull entityToDtoFull(Order order);

    @Mapping(target = "userInfo", source = "user")
    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "createdAt", source = "order.createdAt")
    @Mapping(target = "updatedAt", source = "order.updatedAt")
    public abstract OrderDtoResponse entityToDto(Order order, UserInfo user);

    UserInfo getUserInfo(Long id) {
        return userClient.getUserById(id);
    }
}
