package org.datn.petcare.mapper;

import javax.annotation.processing.Generated;
import org.datn.petcare.dto.GroupServiceDTO;
import org.datn.petcare.entity.GroupService;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T21:25:40+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class GroupServiceMapperImpl implements GroupServiceMapper {

    @Override
    public GroupServiceDTO toDto(GroupService groupService) {
        if ( groupService == null ) {
            return null;
        }

        GroupServiceDTO.GroupServiceDTOBuilder groupServiceDTO = GroupServiceDTO.builder();

        groupServiceDTO.id( groupService.getId() );
        groupServiceDTO.name( groupService.getName() );

        return groupServiceDTO.build();
    }

    @Override
    public GroupService toEntity(GroupServiceDTO groupServiceDTO) {
        if ( groupServiceDTO == null ) {
            return null;
        }

        GroupService.GroupServiceBuilder groupService = GroupService.builder();

        groupService.id( groupServiceDTO.getId() );
        groupService.name( groupServiceDTO.getName() );

        return groupService.build();
    }
}
