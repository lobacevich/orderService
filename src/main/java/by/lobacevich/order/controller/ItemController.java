package by.lobacevich.order.controller;

import by.lobacevich.order.dto.request.ItemDtoRequest;
import by.lobacevich.order.dto.response.ItemDtoResponse;
import by.lobacevich.order.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Items", description = "Item management endpoints")
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @Operation(
            summary = "Get item by ID",
            description = "Retrieves a single item by its ID. Public endpoint."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ItemDtoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
            summary = "Get all items (paginated)",
            description = "Retrieves a page of items. Public endpoint."
    )
    @GetMapping
    public ResponseEntity<Page<ItemDtoResponse>> getAll(
            @RequestParam(value = "size", defaultValue = "20", required = false) int size,
            @RequestParam(value = "number", defaultValue = "0", required = false) int number) {
        return ResponseEntity.ok(service.getAll(number, size));
    }

    @Operation(
            summary = "Create a new item",
            description = "Creates a new item. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ItemDtoResponse> create(@Valid @RequestBody ItemDtoRequest dtoRequest) {
        return new ResponseEntity<>(service.create(dtoRequest), HttpStatus.CREATED);
    }
}
