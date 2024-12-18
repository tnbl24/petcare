package org.datn.petcare.mapper;

import org.datn.petcare.dto.PetDTO;
import org.datn.petcare.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PetMapper {
    @Mapping(source = "userId",target = "user.id")
    Pet toEntity(PetDTO petDTO);
    @Mapping(source = "user.id",target = "userId")
    PetDTO toDTO(Pet pet);
}
