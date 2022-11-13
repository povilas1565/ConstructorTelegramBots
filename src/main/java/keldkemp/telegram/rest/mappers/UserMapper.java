package keldkemp.telegram.rest.mappers;

import keldkemp.telegram.models.Users;
import keldkemp.telegram.rest.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "password", ignore = true)
    public abstract UserDto toUserDtoFromPo(Users user);

    public abstract Users toUserPoFromDto(UserDto userDto);
}
