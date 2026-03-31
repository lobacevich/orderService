package by.lobacevich.order.client;

import by.lobacevich.order.dto.UserInfo;
import by.lobacevich.order.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Log4j2
@Service
public class UserClient {

    public static final String URL = "http://localhost:8082/users/{id}";
    private static final String HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public UserInfo getUserById(Long id) {
        try {
            WebClient webClient = WebClient.builder().build();
            return webClient.get()
                    .uri(URL, id)
                    .header(HEADER, TOKEN_PREFIX + SecurityUtils.getPrincipal().token())
                    .retrieve()
                    .bodyToMono(UserInfo.class)
                    .block();
        } catch (Exception e) {
            log.error("{}, {}, {}", e.getMessage(), e.getClass().getSimpleName(), e.getStackTrace());
            return null;
        }
    }
}
