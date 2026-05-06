package by.lobacevich.order.client;

import by.lobacevich.order.dto.request.UserIds;
import by.lobacevich.order.dto.response.UserInfo;
import by.lobacevich.order.exception.ClientErrorException;
import by.lobacevich.order.exception.ServerErrorException;
import by.lobacevich.order.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    private static final Long ID = 1L;
    private static final String USER_ROLE = "USER_ROLE";
    private static final String URI = "/users/{id}";
    private static final String BATCH_URI = "/users/batch";
    private static final String SUCCESS_BODY = "{\"id\":1,\"name\":\"John Doe\"}";
    private static final String SUCCESS_LIST_BODY = "[{\"id\":1},{\"id\":2}]";
    private static final String BAD_REQUEST = "Bad request";
    private static final String SERVER_ERROR = "Server error";
    private static final List<Long> ID_LIST = List.of(1L, 2L);

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> uriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec bodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpec;

    @InjectMocks
    private UserClient userClient;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setup() {
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(ID);
        mockedSecurityUtils.when(SecurityUtils::getCurrentRole).thenReturn(USER_ROLE);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void getUserById_ShouldReturnUserInfo() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(URI, ID);
        doReturn(headersSpec).when(headersSpec).header(anyString(), anyString());

        when(headersSpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {
                    Function<ClientResponse, Mono<UserInfo>> func = invocation.getArgument(0);

                    ClientResponse response = ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(SUCCESS_BODY)
                            .build();

                    return func.apply(response);
                });
        UserInfo actual = userClient.getUserById(ID);

        assertEquals(ID, actual.id());
        assertEquals("John Doe", actual.name());
    }

    @Test
    void getUserById_ShouldThrowClientErrorException() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(URI, ID);
        doReturn(headersSpec).when(headersSpec).header(anyString(), anyString());

        when(headersSpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {
                    Function<ClientResponse, Mono<UserInfo>> func = invocation.getArgument(0);

                    ClientResponse response = ClientResponse.create(HttpStatus.BAD_REQUEST)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(BAD_REQUEST)
                            .build();

                    return func.apply(response);
                });
        assertThrows(ClientErrorException.class, () -> userClient.getUserById(ID));
    }

    @Test
    void getUserById_ShouldThrowServerErrorException() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(URI, ID);
        doReturn(headersSpec).when(headersSpec).header(anyString(), anyString());

        when(headersSpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {
                    Function<ClientResponse, Mono<UserInfo>> func = invocation.getArgument(0);

                    ClientResponse response = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(SERVER_ERROR)
                            .build();

                    return func.apply(response);
                });
        assertThrows(ServerErrorException.class, () -> userClient.getUserById(ID));
    }

    @Test
    void getUsersByIds_ShouldReturnListOfUserInfo() {
        doReturn(bodyUriSpec).when(webClient).post();
        doReturn(bodyUriSpec).when(bodyUriSpec).uri(BATCH_URI);
        doReturn(headersSpec).when(bodyUriSpec).bodyValue(any(UserIds.class));
        doReturn(headersSpec).when(headersSpec).header(anyString(), anyString());

        when(headersSpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {
                    Function<ClientResponse, Mono<UserInfo>> func = invocation.getArgument(0);

                    ClientResponse response = ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(SUCCESS_LIST_BODY)
                            .build();

                    return func.apply(response);
                });

        List<UserInfo> actual = userClient.getUsersByIds(ID_LIST);

        assertEquals(2, actual.size());
    }

    @Test
    void getUserByIds_ShouldThrowClientErrorException() {
        doReturn(bodyUriSpec).when(webClient).post();
        doReturn(bodyUriSpec).when(bodyUriSpec).uri(BATCH_URI);
        doReturn(headersSpec).when(bodyUriSpec).bodyValue(any(UserIds.class));
        doReturn(headersSpec).when(headersSpec).header(anyString(), anyString());

        when(headersSpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {
                    Function<ClientResponse, Mono<UserInfo>> func = invocation.getArgument(0);

                    ClientResponse response = ClientResponse.create(HttpStatus.BAD_REQUEST)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(BAD_REQUEST)
                            .build();

                    return func.apply(response);
                });
        assertThrows(ClientErrorException.class, () -> userClient.getUsersByIds(ID_LIST));
    }

    @Test
    void getUserByIds_ShouldThrowServerErrorException() {
        doReturn(bodyUriSpec).when(webClient).post();
        doReturn(bodyUriSpec).when(bodyUriSpec).uri(BATCH_URI);
        doReturn(headersSpec).when(bodyUriSpec).bodyValue(any(UserIds.class));
        doReturn(headersSpec).when(headersSpec).header(anyString(), anyString());

        when(headersSpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {
                    Function<ClientResponse, Mono<UserInfo>> func = invocation.getArgument(0);

                    ClientResponse response = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(SERVER_ERROR)
                            .build();

                    return func.apply(response);
                });
        assertThrows(ServerErrorException.class, () -> userClient.getUsersByIds(ID_LIST));
    }
}
