package keldkemp.telegram.rest.controllers;

import keldkemp.telegram.rest.dto.UserDto;
import keldkemp.telegram.rest.mappers.UserMapper;
import keldkemp.telegram.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @RequestMapping(method = RequestMethod.GET)
    public UserDto getCurrentUser() {
        return userMapper.toUserDtoFromPo(userService.getCurrentUser());
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserDto saveCurrentUser(@RequestBody UserDto userDto) {
        return userMapper.toUserDtoFromPo(userService.save(userMapper.toUserPoFromDto(userDto)));
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public UserDto changePassword(@RequestBody UserDto userDto) {
        return userMapper.toUserDtoFromPo(userService.changePassword(userMapper.toUserPoFromDto(userDto)));
    }
}
