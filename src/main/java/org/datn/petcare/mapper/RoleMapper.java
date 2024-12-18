package org.datn.petcare.mapper;

import org.datn.petcare.dto.RoleDTO;
import org.datn.petcare.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleDTO roleDTO);
}
