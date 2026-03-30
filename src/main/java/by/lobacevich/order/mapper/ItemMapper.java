package by.lobacevich.order.mapper;

import by.lobacevich.order.dto.request.ItemDtoRequest;
import by.lobacevich.order.dto.response.ItemDtoResponse;
import by.lobacevich.order.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    Item dtoToEntity(ItemDtoRequest dtoRequest);

    ItemDtoResponse entityToDto(Item item);
}
