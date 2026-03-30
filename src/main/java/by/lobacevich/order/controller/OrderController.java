package by.lobacevich.order.controller;

import by.lobacevich.order.dto.request.OrderCreateDtoRequest;
import by.lobacevich.order.dto.request.OrderUpdateDtoRequest;
import by.lobacevich.order.dto.request.StatusDtoRequest;
import by.lobacevich.order.dto.response.OrderDtoResponse;
import by.lobacevich.order.dto.response.OrderDtoResponseFull;
import by.lobacevich.order.entity.enums.OrderStatus;
import by.lobacevich.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/orders"))
public class OrderController {

    private final OrderService service;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDtoResponseFull> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDtoResponse>> getAll(
            @RequestParam(value = "deleted", required = false) Boolean deleted,
            @RequestParam(value = "statuses", required = false) List<OrderStatus> statuses,
            @RequestParam(value = "from", required = false) LocalDateTime from,
            @RequestParam(value = "to", required = false) LocalDateTime to,
            @RequestParam(value = "size", defaultValue = "20", required = false) int size,
            @RequestParam(value = "number", defaultValue = "0", required = false) int number) {
        return ResponseEntity.ok(service.getAll(deleted, statuses, from, to, number, size));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<OrderDtoResponse>> getByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByUserId(id));
    }

    @PostMapping
    public ResponseEntity<OrderDtoResponseFull> create(@Valid @RequestBody OrderCreateDtoRequest dtoRequest) {
        return new ResponseEntity<>(service.create(dtoRequest), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDtoResponseFull> setStatus(@Valid @RequestBody StatusDtoRequest statusDto,
                          @PathVariable Long id) {
        return ResponseEntity.ok(service.setStatus(statusDto, id));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/delete")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDtoResponseFull> update(@Valid @RequestBody OrderUpdateDtoRequest dtoRequest,
                                                       @PathVariable Long id) {
        return ResponseEntity.ok(service.update(dtoRequest, id));
    }
}
