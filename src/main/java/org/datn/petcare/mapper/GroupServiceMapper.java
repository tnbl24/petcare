package org.datn.petcare.mapper;

import org.datn.petcare.dto.GroupServiceDTO;
import org.datn.petcare.entity.GroupService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GroupServiceMapper {
    GroupServiceDTO toDto(GroupService groupService);
    GroupService toEntity(GroupServiceDTO groupServiceDTO);
}
