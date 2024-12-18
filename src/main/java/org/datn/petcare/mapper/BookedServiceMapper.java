package org.datn.petcare.mapper;

import org.datn.petcare.dto.BookedServiceDTO;
import org.datn.petcare.entity.BookedService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookedServiceMapper {
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "serviceId", target = "services.id")
    @Mapping(source = "petId", target = "pet.id")
    @Mapping(source = "ratingId", target = "rating.id") // Ánh xạ ID đánh giá nếu cần
    BookedService toEntity(BookedServiceDTO serviceDTO);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "services.id", target = "serviceId")
    @Mapping(source = "pet.id", target = "petId")
    @Mapping(source = "rating.id", target = "ratingId") // Ánh xạ ID đánh giá nếu cần
    BookedServiceDTO toDTO(BookedService service);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(BookedServiceDTO serviceDTO, @MappingTarget BookedService entity);
}
