package by.lobacevich.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ServerErrorException extends RuntimeException {

    private final HttpStatusCode status;

    public ServerErrorException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }
}
