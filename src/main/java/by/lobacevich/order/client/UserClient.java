package by.lobacevich.order.client;

import by.lobacevich.order.dto.UserInfo;
import by.lobacevich.order.security.SecurityUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
@Service
public class UserClient {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ROLE = "X-Role";

    @Value("${userClient.url}")
    private String baseUrl;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallBackUser")
    public UserInfo getUserById(Long id) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl).build();
        return webClient.get()
                .uri("/users/{id}", id)
                .header(HEADER_USER_ID, String.valueOf(SecurityUtils.getCurrentUserId()))
                .header(HEADER_ROLE, SecurityUtils.getCurrentRole())
                .retrieve()
                .bodyToMono(UserInfo.class)
                .block();
    }

    private UserInfo fallBackUser(Long id, Exception e) {
        log.warn("{}, {}, {}", e.getMessage(), e.getClass().getSimpleName(), e.getStackTrace());
        return null;
    }
}
