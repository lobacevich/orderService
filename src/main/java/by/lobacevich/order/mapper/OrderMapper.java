package by.lobacevich.order.mapper;

import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.entity.Order;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {

    OrderDtoResponseFull entityToDtoFull(Order order);

    OrderDtoResponse entityToDto(Order order);
}
