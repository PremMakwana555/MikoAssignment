package ai.miko.assignment.Exception;

import ai.miko.assignment.enums.Status;
import ai.miko.assignment.model.QueryStatus;
import ai.miko.assignment.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    RedisRepository repository;

    @ExceptionHandler(MiKoException.class)
    public ResponseEntity<errorMessage> handle(MiKoException exception) {
        repository.save(QueryStatus.builder().status(Status.FAILURE).query(exception.getQuery()).build());
        return new ResponseEntity<>(errorMessage.builder()
                .message(exception.getMessage())
                .build(), exception.getHttpStatus());
    }
}
