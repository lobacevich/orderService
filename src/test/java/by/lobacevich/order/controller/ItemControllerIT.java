package by.lobacevich.order.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ItemControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldCreateItemAndReturnItemDtoResponse() throws Exception {
        String createJson = """
                {
                  "name": "Laptop",
                  "price": 499.99
                }
                """;
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(499.99));
    }

    @Test
    @WithMockUser
    void create_ShouldReturnForbiddenStatusCode() throws Exception {
        String createJson = """
                {
                  "name": "Laptop",
                  "price": 499.99
                }
                """;
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser
    void getById_ShouldReturnItemDtoResponseAndOkStatusCode() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_ShouldReturnUnauthorizedStatusCode() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnListOfItemDtoResponse() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

    }
}