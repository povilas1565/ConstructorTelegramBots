package keldkemp.telegram.services.impl;

import keldkemp.telegram.models.Users;
import keldkemp.telegram.repositories.UsersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {UserServiceImpl.class})
public class UserServiceImplTest {
    @MockBean
    UsersRepository usersRepository;
    @MockBean
    PasswordEncoder passwordEncoder;
    @Autowired
    UserServiceImpl userServiceImpl;


    @Test
    public void testFindCurrentUserNull() {
        when(usersRepository.findByUsername(anyString())).thenReturn(new Users());

        Optional<Users> result = userServiceImpl.findCurrentUser();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindCurrentUserAuthNull() {
        when(usersRepository.findByUsername(anyString())).thenReturn(new Users());

        Optional<Users> result = userServiceImpl.findCurrentUser(null);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCurrentUserException() {
        RuntimeException exception = Assertions.assertThrows(
          RuntimeException.class, () -> userServiceImpl.getCurrentUser()
        );
        Assertions.assertEquals("Unable to find current user!", exception.getMessage());
    }

    @Test
    public void testGetByUsername() {
        Users user = new Users();
        user.setUsername("username");
        user.setId(1L);

        when(usersRepository.findByUsername(anyString())).thenReturn(user);

        Users result = userServiceImpl.getByUsername("username");
        Assertions.assertEquals(user, result);
    }

    @Test
    public void testFindByUsername() {
        Optional<Users> result = userServiceImpl.findByUsername("username");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testSaveUserException() {
        Users user = new Users();
        user.setId(2L);
        user.setName("name");
        user.setUsername("username");
        user.setPassword("123456qwerty");

        when(usersRepository.findByUsername("username")).thenReturn(user);

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class, () -> userServiceImpl.save(user)
        );
        Assertions.assertEquals("Unable to find current user!", exception.getMessage());
    }

    @Test
    public void testCreateUser() {
        Users user = new Users();
        user.setId(2L);
        user.setName("name");
        user.setUsername("username");
        user.setPassword("123456qwerty");

        when(usersRepository.findByUsername(anyString())).thenReturn(null);
        when(usersRepository.save(user)).thenReturn(user);

        Users result = userServiceImpl.createUser(user);
        Assertions.assertEquals(user, result);
    }
}
