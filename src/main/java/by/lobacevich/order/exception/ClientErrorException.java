package by.lobacevich.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ClientErrorException extends RuntimeException {

    private final HttpStatusCode status;

    public ClientErrorException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }
}
