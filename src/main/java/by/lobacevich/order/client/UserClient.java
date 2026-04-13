package by.lobacevich.order.client;

import by.lobacevich.order.dto.request.UserIds;
import by.lobacevich.order.dto.response.UserInfo;
import by.lobacevich.order.exception.ClientErrorException;
import by.lobacevich.order.exception.ServerErrorException;
import by.lobacevich.order.security.SecurityUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
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
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(UserInfo.class);
                    }
                    if (response.statusCode().is4xxClientError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientErrorException(body)));
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new ServerErrorException(body)));
                })
                .block();
    }

    private UserInfo fallBackUser(Long id, Throwable e) {
        log.warn("{}, {}", id, e.getStackTrace());
        return null;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallBackUsers")
    public List<UserInfo> getUsersByIds(List<Long> ids) {
        return webClient.post()
                .uri("/users/batch")
                .bodyValue(new UserIds(ids))
                .header(HEADER_USER_ID, String.valueOf(SecurityUtils.getCurrentUserId()))
                .header(HEADER_ROLE, SecurityUtils.getCurrentRole())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(UserInfo[].class)
                                .map(Arrays::asList);
                    }
                    if (response.statusCode().is4xxClientError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientErrorException(body)));
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new ServerErrorException(body)));
                })
                .block();
    }

    private List<UserInfo> fallBackUsers(List<Long> ids, Throwable e) {
        log.warn("{}, {}", ids, e.getStackTrace());
        return Collections.emptyList();
    }
}
