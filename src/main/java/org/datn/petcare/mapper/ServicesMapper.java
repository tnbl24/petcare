package org.datn.petcare.mapper;

import org.datn.petcare.dto.ServiceDTO;
import org.datn.petcare.entity.Services;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServicesMapper {

    @Mapping(source = "groupService.id", target = "groupServiceId")
    @Mapping(target = "serviceDetails", source = "serviceDetails")
    ServiceDTO toDTO(Services services);

    @Mapping(source = "groupServiceId", target = "groupService.id")
    @Mapping(target = "serviceDetails", source = "serviceDetails")
    Services toEntity(ServiceDTO dto);
}
