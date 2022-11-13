package keldkemp.telegram.services.impl;

import keldkemp.telegram.models.Users;
import keldkemp.telegram.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("baseUserDetailsService")
public class BaseUserDetailService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(BaseUserDetailService.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        logger.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase();
        return findUser(lowercaseLogin)
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
                "database"));
    }

    private Optional<Users> findUser(String login) {
        return userService.findByUsername(login);
    }
}
