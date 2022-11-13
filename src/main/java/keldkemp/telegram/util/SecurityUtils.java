package keldkemp.telegram.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for Spring Security.
 */
public class SecurityUtils {
    public static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }

        Object principal = auth.getPrincipal();
        return !(principal instanceof String) || !ANONYMOUS_USER.equalsIgnoreCase((String) principal);
    }

    /**
     * Get user name.
     *
     * @return user name
     */
    public static String getUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return "CNull";
        }
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return "ANull";
        }
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return "PNull";
        }
        return principal.toString();
    }
}
