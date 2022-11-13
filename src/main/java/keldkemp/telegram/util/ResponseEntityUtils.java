package keldkemp.telegram.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

public class ResponseEntityUtils {

    public static <T> ResponseEntity<T> badRequest() {
        return badRequest(null);
    }

    public static <T> ResponseEntity<T> badRequest(T body) {
        return entity(BAD_REQUEST, body);
    }

    public static <T> ResponseEntity<T> internalServerError(T body) {
        return entity(INTERNAL_SERVER_ERROR, body);
    }

    public static <T> ResponseEntity<T> okRequest() { return okRequest(null); }

    public static <T> ResponseEntity<T> okRequest(T body) { return entity(OK, body); }

    public static <T> ResponseEntity<T> entity(HttpStatus status, T body) {
        return ResponseEntity.status(status).body(body);
    }
}
