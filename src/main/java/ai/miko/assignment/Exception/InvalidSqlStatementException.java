package ai.miko.assignment.Exception;

import org.springframework.http.HttpStatus;

public class InvalidSqlStatementException extends MiKoException {
    public InvalidSqlStatementException(String message, String query) {
        super(message, HttpStatus.BAD_REQUEST, query);
    }
}
