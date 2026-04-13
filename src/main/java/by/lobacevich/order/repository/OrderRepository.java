package by.lobacevich.order.repository;

import by.lobacevich.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    @Override
    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long id);

    @Modifying
    @Query(value = "UPDATE Order o SET o.deleted = true WHERE o.id = :id")
    int delete(@Param("id") Long id);
}
