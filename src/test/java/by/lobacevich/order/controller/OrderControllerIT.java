package by.lobacevich.order.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class OrderControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("webClient.url", () -> wireMock.baseUrl());
    }

    @BeforeEach
    void setWireMock() {
        wireMock.stubFor(WireMock.get(WireMock.urlMatching("/users/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)));
    }

    @Test
    void create_ShouldCreateOrderAndReturnStatusCodeCreated() throws Exception {
        String createJson = """
                {
                    "orderItems": [
                        {
                            "itemId": 1,
                            "quantity": 2
                        }
                    ]
                }
                """;
        mockMvc.perform(post("/orders")
                        .header("X-User-Id", "1")
                        .header("X-Role", "ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2));

        wireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/users/1")));
    }

    @Test
    void update_ShouldUpdateOrderAndReturnStatusCodeOk() throws Exception {
        String updateJson = """
                {
                    "orderItems": [
                        {
                            "itemId": 1,
                            "quantity": 3
                        }
                    ]
                }
                """;
        mockMvc.perform(put("/orders/1")
                        .header("X-User-Id", "1")
                        .header("X-Role", "ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderItems[0].quantity").value(3));

        wireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/users/1")));
    }

    @Test
    void getById_ShouldReturnOrderDtoResponseFullAndStatusCodeOk() throws Exception {
        mockMvc.perform(get("/orders/1")
                        .header("X-User-Id", "1")
                        .header("X-Role", "ROLE_USER"))
                .andExpect(status().isOk());

        wireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/users/1")));
    }

    @Test
    void getById_ShouldReturnStatusCodeUnauthorized() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_ShouldReturnListOfOrderDtoResponse() throws Exception {
        wireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/users/batch"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));
        mockMvc.perform(get("/orders")
                        .header("X-User-Id", "1")
                        .header("X-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        wireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/users/batch")));
    }

    @Test
    void getAll_ShouldReturnStatusCodeForbidden() throws Exception {
        mockMvc.perform(get("/orders")
                        .header("X-User-Id", "1")
                        .header("X-Role", "ROLE_USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getByUserId_ShouldReturnListOfOrderDtoResponse() throws Exception {
        mockMvc.perform(get("/orders/user/1")
                        .header("X-User-Id", "1")
                        .header("X-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        wireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/users/1")));
    }


    @Test
    void setStatus_ShouldSetOrderStatusAndReturnStatusCodeOk() throws Exception {
        String patchStatusJson = """
                {
                    "status": "CANCELED"
                }
                """;
        mockMvc.perform(patch("/orders/1/status")
                        .header("X-User-Id", "1")
                        .header("X-Role", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));

        wireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/users/1")));
    }

    @Test
    void delete_ShouldReturnStatusCodeNoContent() throws Exception {
        mockMvc.perform(delete("/orders/1")
                        .header("X-User-Id", "1")
                        .header("X-Role", "ROLE_ADMIN"))
                .andExpect(status().isNoContent());
    }
}
