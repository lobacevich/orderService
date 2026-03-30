package by.lobacevich.order.specification;

import by.lobacevich.order.entity.Order;
import by.lobacevich.order.entity.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class OrderSpecification {

    public static final String DELETED = "deleted";
    public static final String STATUS = "status";
    public static final String CREATED_AT = "createdAt";

    private OrderSpecification() {
    }

    public static Specification<Order> notDeleted(Boolean deleted) {
        return (root, query, cb) -> deleted == null
               ? cb.conjunction()
               : cb.equal(root.get(DELETED), deleted);
    }

    public static Specification<Order> hasStatus(List<OrderStatus> statuses) {
        return (root, query, cb) -> statuses == null || statuses.isEmpty()
                ? cb.conjunction()
                : root.get(STATUS).in(statuses);
    }

    public static Specification<Order> createdBetween(LocalDateTime from,
                                                      LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return cb.conjunction();
            }
            if (from != null && to != null)
                return cb.between(root.get(CREATED_AT), from, to);
            if (from != null)
                return cb.greaterThanOrEqualTo(root.get(CREATED_AT), from);
            return cb.lessThanOrEqualTo(root.get(CREATED_AT), to);
        };
    }

    public static Specification<Order> filterBy(Boolean deleted,
                                                List<OrderStatus> statuses,
                                                LocalDateTime from,
                                                LocalDateTime to) {
        return notDeleted(deleted)
                .and(hasStatus(statuses))
                .and(createdBetween(from, to));
    }
}
