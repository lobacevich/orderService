package by.lobacevich.order.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Order Service API", version = "1.0")
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer addGlobalHeaders() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(new HeaderParameter()
                    .name("X-User-Id")
                    .description("User ID (provided by API Gateway)")
                    .required(true)
                    .example("1"));
            operation.addParametersItem(new HeaderParameter()
                    .name("X-Role")
                    .description("User role (ROLE_USER, ROLE_ADMIN)")
                    .required(true)
                    .example("ROLE_USER"));
            return operation;
        };
    }
}
