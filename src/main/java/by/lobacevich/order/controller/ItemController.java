package by.lobacevich.order.controller;

import by.lobacevich.order.dto.request.ItemDtoRequest;
import by.lobacevich.order.dto.response.ItemDtoResponse;
import by.lobacevich.order.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @GetMapping("/{id}")
    public ResponseEntity<ItemDtoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ItemDtoResponse>> getAll(
            @RequestParam(value = "size", defaultValue = "20", required = false) int size,
            @RequestParam(value = "number", defaultValue = "0", required = false) int number) {
        return ResponseEntity.ok(service.getAll(number, size));
    }

    @PostMapping
    public ResponseEntity<ItemDtoResponse> create(@Valid @RequestBody ItemDtoRequest dtoRequest) {
        return new ResponseEntity<>(service.create(dtoRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDtoResponse> update(@Valid @RequestBody ItemDtoRequest dtoRequest,
                                                     @PathVariable Long id) {
        return ResponseEntity.ok(service.update(dtoRequest, id));
    }
}
