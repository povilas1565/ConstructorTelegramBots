package keldkemp.telegram.rest;

import keldkemp.telegram.rest.dto.ErrorServerDto;
import keldkemp.telegram.rest.dto.NodeDto;
import keldkemp.telegram.util.LocalhostUtils;
import keldkemp.telegram.util.ResponseEntityUtils;
import keldkemp.telegram.util.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.JDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * See also {@link NonRestErrorHandler}.
 */
@ControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestErrorHandler.class);

    private void fillMdc(UUID uuid, String user) {
        MDC.put("ErrorUuid", uuid.toString());
        MDC.put("UserName", user);
    }

    private void logWarn(ErrorServerDto<?> error, Throwable exception, String text) {
        UUID uuid = error.getGuid();
        String user = SecurityUtils.getUser();

        fillMdc(uuid, user);
        logger.warn("RUID:" + uuid + ", User:" + user + ", Url:" + error.getUrl() +
                ", " + text, exception);
    }

    private void logError(ErrorServerDto<?> error, Throwable exception) {
        UUID uuid = error.getGuid();
        String user = SecurityUtils.getUser();

        fillMdc(uuid, user);
        logger.error("RUID:" + uuid + ", User:" + user + ", Url:" + error.getUrl() +
                ", Exception raised", exception);
    }

    private boolean isRequestExpectingToReAuth(HttpServletRequest request) {
        if (request.getMethod().equals("POST") && request.getHeader("content-length").equals("0")) {
            String authorizationHeader = request.getHeader("Authorization");
            return authorizationHeader != null && authorizationHeader.startsWith("NTLM");
        }
        return false;
    }

    private ErrorServerDto<?> makeServerError() {
        ErrorServerDto<?> error = new ErrorServerDto<>()
                .setGuid(UUID.randomUUID());
        updateNodeName(error);
        return error;
    }

    /**
     * Append error stack or not.
     */
    //TODO
    private boolean appendErrorStack() {
        return true;
    }

    private void updateStack(ErrorServerDto<?> error, Throwable exception) {
        if (appendErrorStack()) {
            String stack = ExceptionUtils.getStackTrace(exception);
            error.setStack(stack);
        }
    }

    private void updateServerMessage(ErrorServerDto<?> error, Throwable exception) {
        if (appendErrorStack()) {
            String message = exception.getLocalizedMessage();
            if (exception instanceof NestedRuntimeException) {
                String potentialMessage = ((NestedRuntimeException) exception).getMostSpecificCause().getMessage();
                if (potentialMessage != null) {
                    message = potentialMessage;
                }
            } else if (exception instanceof JDBCException) {
                message = ((JDBCException) exception).getSQLException().getMessage();
            }

            if (message == null) {
                //TODO
                message = error.getMessage();
            }

            error.setMessage(message);
        } else {
            //TODO
            error.setMessage(error.getMessage());
        }
    }

    /**
     * Make error by the exception and logging.
     *
     * @param exception exception to process
     * @return error
     */
    public ErrorServerDto<?> processException(Throwable exception) {
        ErrorServerDto<?> error = makeServerError();

        updateServerMessage(error, exception);
        updateStack(error, exception);

        logError(error, exception);

        return error;
    }

    /**
     * Handle all other exceptions that is not already handled.
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorServerDto<?>> handleException(HttpServletRequest request, HttpServletResponse response, Throwable exception) {
        ErrorServerDto<?> error = processException(exception);

        return ResponseEntityUtils.internalServerError(error);
    }

    private void updateNodeName(ErrorServerDto<?> error) {
        String name = LocalhostUtils.INSTANCE.getHostName();
        if (!StringUtils.isEmpty(name)) {
            NodeDto node = new NodeDto();
            node.setName(name);
            error.setNode(node);
        }
    }
}
