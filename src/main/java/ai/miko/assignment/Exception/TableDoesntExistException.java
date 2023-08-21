package ai.miko.assignment.Exception;

import org.springframework.http.HttpStatus;

public class TableDoesntExistException extends MiKoException {
    public TableDoesntExistException(String message, String query) {
        super(message, HttpStatus.NOT_FOUND, query);
    }
}
