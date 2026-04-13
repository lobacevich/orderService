package by.lobacevich.order.controller;

import by.lobacevich.order.dto.request.OrderDtoRequest;
import by.lobacevich.order.dto.request.StatusDtoRequest;
import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.entity.enums.OrderStatus;
import by.lobacevich.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Orders", description = "Order management endpoints")
@RequiredArgsConstructor
@RestController
@RequestMapping(("/orders"))
public class OrderController {

    private final OrderService service;

    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a single order by its ID. Requires ADMIN role or ownership."
    )
    @PreAuthorize("hasRole('ADMIN') or @orderServiceImpl.isOwner(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDtoResponseFull> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
            summary = "Get all orders (paginated)",
            description = "Retrieves a page of orders with optional filters. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OrderDtoResponse>> getAll(
            @RequestParam(value = "deleted", required = false) Boolean deleted,
            @RequestParam(value = "statuses", required = false) List<OrderStatus> statuses,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            @RequestParam(value = "size", defaultValue = "20", required = false) int size,
            @RequestParam(value = "number", defaultValue = "0", required = false) int number) {
        return ResponseEntity.ok(service.getAll(deleted, statuses, from, to, number, size));
    }

    @Operation(
            summary = "Get orders by user ID",
            description = "Retrieves all orders for a given user. Requires ADMIN role or ownership."
    )
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId()")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<OrderDtoResponse>> getByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByUserId(id));
    }

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order with the given details. Requires authentication"
    )
    @PostMapping
    public ResponseEntity<OrderDtoResponseFull> create(@Valid @RequestBody OrderDtoRequest dtoRequest) {
        return new ResponseEntity<>(service.create(dtoRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update order status",
            description = "Updates the status of an existing order. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDtoResponseFull> setStatus(@Valid @RequestBody StatusDtoRequest statusDto,
                                                          @PathVariable Long id) {
        return ResponseEntity.ok(service.setStatus(statusDto, id));
    }

    @Operation(
            summary = "Delete order",
            description = "Soft-deletes an order by ID. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(
            summary = "Update order",
            description = "Fully updates an existing order. Requires ADMIN role or ownership."
    )
    @PreAuthorize("hasRole('ADMIN') or @orderServiceImpl.isOwner(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<OrderDtoResponseFull> update(@Valid @RequestBody OrderDtoRequest dtoRequest,
                                                       @PathVariable Long id) {
        return ResponseEntity.ok(service.update(dtoRequest, id));
    }
}
