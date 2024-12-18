package org.datn.petcare.mapper;

import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.role",target = "role" )
    UserDTO toDTO(User user);
    @Mapping(source = "role",target = "role.role")
    User toEntity(UserDTO userDTO);

}
