package by.lobacevich.order.client;

import by.lobacevich.order.dto.UserIds;
import by.lobacevich.order.dto.UserInfo;
import by.lobacevich.order.security.SecurityUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Service
public class UserClient {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ROLE = "X-Role";

    private final WebClient webClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallBackUser")
    public UserInfo getUserById(Long id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .header(HEADER_USER_ID, String.valueOf(SecurityUtils.getCurrentUserId()))
                .header(HEADER_ROLE, SecurityUtils.getCurrentRole())
                .retrieve()
                .bodyToMono(UserInfo.class)
                .block();
    }

    private UserInfo fallBackUser(Long id, Throwable e) {
        log.warn("{}, {}, {}", e.getMessage(), e.getClass().getSimpleName(), e.getStackTrace());
        return null;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallBackUsers")
    public List<UserInfo> getUsersByIds(List<Long> ids) {
        return webClient.post()
                .uri("/users/batch")
                .bodyValue(new UserIds(ids))
                .header(HEADER_USER_ID, String.valueOf(SecurityUtils.getCurrentUserId()))
                .header(HEADER_ROLE, SecurityUtils.getCurrentRole())
                .retrieve()
                .bodyToFlux(UserInfo.class)
                .collectList()
                .block();
    }

    private List<UserInfo> fallBackUsers(List<Long> ids, Throwable e) {
        log.warn("{}, {}, {}", e.getMessage(), e.getClass().getSimpleName(), e.getStackTrace());
        return Collections.emptyList();
    }
}
