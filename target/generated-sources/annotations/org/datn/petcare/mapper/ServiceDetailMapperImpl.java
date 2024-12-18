package org.datn.petcare.mapper;

import javax.annotation.processing.Generated;
import org.datn.petcare.dto.ServiceDetailDTO;
import org.datn.petcare.entity.ServiceDetail;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T21:25:40+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class ServiceDetailMapperImpl implements ServiceDetailMapper {

    @Override
    public ServiceDetailDTO toDTO(ServiceDetail serviceDetail) {
        if ( serviceDetail == null ) {
            return null;
        }

        ServiceDetailDTO.ServiceDetailDTOBuilder serviceDetailDTO = ServiceDetailDTO.builder();

        serviceDetailDTO.id( serviceDetail.getId() );
        serviceDetailDTO.weight( serviceDetail.getWeight() );
        serviceDetailDTO.price( serviceDetail.getPrice() );

        return serviceDetailDTO.build();
    }

    @Override
    public ServiceDetail toEntity(ServiceDetailDTO serviceDetailDTO) {
        if ( serviceDetailDTO == null ) {
            return null;
        }

        ServiceDetail.ServiceDetailBuilder serviceDetail = ServiceDetail.builder();

        serviceDetail.id( serviceDetailDTO.getId() );
        serviceDetail.weight( serviceDetailDTO.getWeight() );
        serviceDetail.price( serviceDetailDTO.getPrice() );

        return serviceDetail.build();
    }
}
