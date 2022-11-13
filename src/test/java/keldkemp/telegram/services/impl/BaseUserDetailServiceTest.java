package keldkemp.telegram.services.impl;

import keldkemp.telegram.models.Users;
import keldkemp.telegram.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.*;

class BaseUserDetailServiceTest {
    @Mock
    UserService userService;
    @InjectMocks
    BaseUserDetailService baseUserDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLoadUserByUsername() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("login");
        user.setName("name");

        when(userService.findByUsername("login")).thenReturn(Optional.of(user));

        UserDetails result = baseUserDetailService.loadUserByUsername("login");
        Assertions.assertEquals(user, result);
    }

    @Test
    void testLoadUserByUsernameEmpty() {
        when(userService.findByUsername("login")).thenReturn(Optional.empty());

        try {
            baseUserDetailService.loadUserByUsername("login");
        } catch (Exception e) {
            Assertions.assertEquals("User login was not found in the database", e.getMessage());
            Assertions.assertEquals(UsernameNotFoundException.class, e.getClass());
        }
    }
}
