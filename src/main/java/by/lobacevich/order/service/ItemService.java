package by.lobacevich.order.service;

import by.lobacevich.order.dto.request.ItemDtoRequest;
import by.lobacevich.order.dto.response.ItemDtoResponse;
import org.springframework.data.domain.Page;

public interface ItemService {

    ItemDtoResponse create(ItemDtoRequest dtoRequest);

    ItemDtoResponse getById(Long id);

    Page<ItemDtoResponse> getAll(int number,
                                 int size);
}
