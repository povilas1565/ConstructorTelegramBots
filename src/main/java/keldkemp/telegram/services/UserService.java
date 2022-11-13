package keldkemp.telegram.services;

import keldkemp.telegram.models.Users;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface UserService {

    /**
     * Find user of {@code username}
     * @return Optional
     */
    Optional<Users> findByUsername(String username);

    /**
     * Get user of {@code username}
     * @return Users
     */
    Users getByUsername(String username);

    /**
     * Get hashed string of {@code input}.
     */
    String getHash(String input);

    /**
     * Change password, current_user.
     * @param user user
     * @return user
     */
    Users changePassword(Users user);

    /**
     * Save current user.
     * @param user user
     * @return Users
     */
    Users save(Users user);

    /**
     * Create new user.
     * @param user - user
     * @return Users
     */
    Users createUser(Users user);

    /**
     * Get current user.
     * @return Users
     */
    Users getCurrentUser();

    /**
     * Find current user.
     * @return Optional
     */
    Optional<Users> findCurrentUser();

    /**
     * find current user.
     * @param authentication authentication
     * @return Optional
     */
    Optional<Users> findCurrentUser(Authentication authentication);
}
