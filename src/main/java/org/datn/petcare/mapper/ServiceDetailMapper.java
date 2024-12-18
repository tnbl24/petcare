package org.datn.petcare.mapper;

import org.datn.petcare.dto.ServiceDTO;
import org.datn.petcare.dto.ServiceDetailDTO;
import org.datn.petcare.entity.ServiceDetail;
import org.datn.petcare.entity.Services;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceDetailMapper {
    ServiceDetailDTO toDTO(ServiceDetail serviceDetail);

    ServiceDetail toEntity(ServiceDetailDTO serviceDetailDTO);
}
