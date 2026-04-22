package by.lobacevich.order.kafka.consumer;

import by.lobacevich.order.dto.request.StatusDtoRequest;
import by.lobacevich.order.entity.enums.OrderStatus;
import by.lobacevich.order.kafka.event.PaymentCreatedEvent;
import by.lobacevich.order.service.impl.OrderTxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class PaymentCreatedEventConsumer {

    private static final String TOPIC = "CREATE_PAYMENT";
    private static final String SUCCESS = "SUCCESS";

    private final OrderTxService orderService;

    @KafkaListener(topics = TOPIC, groupId = "order-group")
    public void handlePaymentCreatedEvent(PaymentCreatedEvent event) {
        log.info("Received event for orderId: {} with status: {}", event.orderId(), event.status());
        if (event.status().equals(SUCCESS)) {
            orderService.setStatusTx(new StatusDtoRequest(OrderStatus.PAID), event.orderId());
        } else {
            orderService.setStatusTx(new StatusDtoRequest(OrderStatus.PAYMENT_FAILED), event.orderId());
        }
    }
}
