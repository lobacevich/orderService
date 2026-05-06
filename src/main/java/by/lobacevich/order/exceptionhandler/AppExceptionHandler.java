package by.lobacevich.order.exceptionhandler;

import by.lobacevich.order.dto.response.ErrorDto;
import by.lobacevich.order.exception.ClientErrorException;
import by.lobacevich.order.exception.EntityNotFoundException;
import by.lobacevich.order.exception.ServerErrorException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Log4j2
@RestControllerAdvice
public class AppExceptionHandler {

    private static final String ERROR_LOG_FRAME = "{}, {}";

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.error(ERROR_LOG_FRAME, e.getMessage(), e.getStackTrace());
        return new ResponseEntity<>(new ErrorDto(String.join(", ", errors)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error(ERROR_LOG_FRAME, e.getMessage(), e.getStackTrace());
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        log.error(ERROR_LOG_FRAME, e.getMessage(), e.getStackTrace());
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleClientErrorException(ClientErrorException e) {
        log.error(ERROR_LOG_FRAME, e.getMessage(), e.getStackTrace());
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), e.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleServerErrorException(ServerErrorException e) {
        log.error(ERROR_LOG_FRAME, e.getMessage(), e.getStackTrace());
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), e.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        log.error(ERROR_LOG_FRAME, e.getMessage(),  e.getStackTrace());
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
