package ai.miko.assignment.Exception;

import org.springframework.http.HttpStatus;

public class TableAlreadyExistsException extends MiKoException {
    public TableAlreadyExistsException(String message, String query) {
        super(message, HttpStatus.BAD_REQUEST, query);
    }
}
