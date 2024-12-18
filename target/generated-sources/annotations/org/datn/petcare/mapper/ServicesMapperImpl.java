package org.datn.petcare.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.processing.Generated;
import org.datn.petcare.dto.ServiceDTO;
import org.datn.petcare.dto.ServiceDetailDTO;
import org.datn.petcare.entity.GroupService;
import org.datn.petcare.entity.ServiceDetail;
import org.datn.petcare.entity.Services;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T21:25:40+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class ServicesMapperImpl implements ServicesMapper {

    @Override
    public ServiceDTO toDTO(Services services) {
        if ( services == null ) {
            return null;
        }

        ServiceDTO.ServiceDTOBuilder serviceDTO = ServiceDTO.builder();

        serviceDTO.groupServiceId( servicesGroupServiceId( services ) );
        serviceDTO.serviceDetails( serviceDetailListToServiceDetailDTOList( services.getServiceDetails() ) );
        serviceDTO.id( services.getId() );
        serviceDTO.name( services.getName() );
        serviceDTO.description( services.getDescription() );
        serviceDTO.price( services.getPrice() );
        byte[] image = services.getImage();
        if ( image != null ) {
            serviceDTO.image( Arrays.copyOf( image, image.length ) );
        }
        serviceDTO.base64Image( services.getBase64Image() );
        serviceDTO.hasDetail( services.getHasDetail() );

        return serviceDTO.build();
    }

    @Override
    public Services toEntity(ServiceDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Services.ServicesBuilder services = Services.builder();

        services.groupService( serviceDTOToGroupService( dto ) );
        services.serviceDetails( serviceDetailDTOListToServiceDetailList( dto.getServiceDetails() ) );
        services.id( dto.getId() );
        services.name( dto.getName() );
        services.description( dto.getDescription() );
        byte[] image = dto.getImage();
        if ( image != null ) {
            services.image( Arrays.copyOf( image, image.length ) );
        }
        services.price( dto.getPrice() );
        services.hasDetail( dto.getHasDetail() );
        services.base64Image( dto.getBase64Image() );

        return services.build();
    }

    private int servicesGroupServiceId(Services services) {
        if ( services == null ) {
            return 0;
        }
        GroupService groupService = services.getGroupService();
        if ( groupService == null ) {
            return 0;
        }
        int id = groupService.getId();
        return id;
    }

    protected ServiceDetailDTO serviceDetailToServiceDetailDTO(ServiceDetail serviceDetail) {
        if ( serviceDetail == null ) {
            return null;
        }

        ServiceDetailDTO.ServiceDetailDTOBuilder serviceDetailDTO = ServiceDetailDTO.builder();

        serviceDetailDTO.id( serviceDetail.getId() );
        serviceDetailDTO.weight( serviceDetail.getWeight() );
        serviceDetailDTO.price( serviceDetail.getPrice() );

        return serviceDetailDTO.build();
    }

    protected List<ServiceDetailDTO> serviceDetailListToServiceDetailDTOList(List<ServiceDetail> list) {
        if ( list == null ) {
            return null;
        }

        List<ServiceDetailDTO> list1 = new ArrayList<ServiceDetailDTO>( list.size() );
        for ( ServiceDetail serviceDetail : list ) {
            list1.add( serviceDetailToServiceDetailDTO( serviceDetail ) );
        }

        return list1;
    }

    protected GroupService serviceDTOToGroupService(ServiceDTO serviceDTO) {
        if ( serviceDTO == null ) {
            return null;
        }

        GroupService.GroupServiceBuilder groupService = GroupService.builder();

        groupService.id( serviceDTO.getGroupServiceId() );

        return groupService.build();
    }

    protected ServiceDetail serviceDetailDTOToServiceDetail(ServiceDetailDTO serviceDetailDTO) {
        if ( serviceDetailDTO == null ) {
            return null;
        }

        ServiceDetail.ServiceDetailBuilder serviceDetail = ServiceDetail.builder();

        serviceDetail.id( serviceDetailDTO.getId() );
        serviceDetail.weight( serviceDetailDTO.getWeight() );
        serviceDetail.price( serviceDetailDTO.getPrice() );

        return serviceDetail.build();
    }

    protected List<ServiceDetail> serviceDetailDTOListToServiceDetailList(List<ServiceDetailDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<ServiceDetail> list1 = new ArrayList<ServiceDetail>( list.size() );
        for ( ServiceDetailDTO serviceDetailDTO : list ) {
            list1.add( serviceDetailDTOToServiceDetail( serviceDetailDTO ) );
        }

        return list1;
    }
}
