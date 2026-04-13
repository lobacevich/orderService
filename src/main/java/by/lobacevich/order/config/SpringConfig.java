package by.lobacevich.order.config;

import by.lobacevich.order.mapper.ItemMapper;
import by.lobacevich.order.mapper.ItemMapperImpl;
import by.lobacevich.order.mapper.OrderMapper;
import by.lobacevich.order.mapper.OrderMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    ItemMapper itemMapper() {
        return new ItemMapperImpl();
    }

    @Bean
    OrderMapper orderMapper() {
        return new OrderMapperImpl();
    }
}
