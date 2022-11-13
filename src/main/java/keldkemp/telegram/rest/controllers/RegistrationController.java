package keldkemp.telegram.rest.controllers;

import keldkemp.telegram.rest.dto.UserDto;
import keldkemp.telegram.rest.mappers.UserMapper;
import keldkemp.telegram.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void createUser(UserDto userDto, HttpServletResponse httpServletResponse) throws IOException {
        UserDto userAfterCreateDto = userMapper.toUserDtoFromPo(userService.createUser(userMapper.toUserPoFromDto(userDto)));
        httpServletResponse.sendRedirect("/login.html");
    }
}
