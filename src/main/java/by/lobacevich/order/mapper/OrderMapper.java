package by.lobacevich.order.mapper;

import by.lobacevich.order.client.UserClient;
import by.lobacevich.order.dto.UserInfo;
import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper {

    @Autowired
    private UserClient userClient;

    @Mapping(target = "userInfo", expression = "java(getUserInfo(order.getUserId()))")
    public abstract OrderDtoResponseFull entityToDtoFull(Order order);

    @Mapping(target = "userInfo", expression = "java(getUserInfo(order.getUserId()))")
    public abstract OrderDtoResponse entityToDto(Order order);

    UserInfo getUserInfo(Long id) {
        return userClient.getUserById(id);
    }
}
