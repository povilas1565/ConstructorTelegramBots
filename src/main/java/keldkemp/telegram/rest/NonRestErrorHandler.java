package keldkemp.telegram.rest;

import keldkemp.telegram.rest.dto.ErrorServerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * See also {@link RestErrorHandler}.
 */
@ControllerAdvice
@Order
public class NonRestErrorHandler {

    @Autowired
    private RestErrorHandler restErrorHandler;

    /**
     * Handle all other exceptions that is not already handled.
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorServerDto<?>> handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return restErrorHandler.handleException(request, response, exception);
    }
}
