package ai.miko.assignment.Exception;

import ai.miko.assignment.model.QueryStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class MiKoException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String query;

    public MiKoException(String message, HttpStatus httpStatus, String query) {
        super(message);
        this.httpStatus = httpStatus;
        this.query = query;
    }
}
