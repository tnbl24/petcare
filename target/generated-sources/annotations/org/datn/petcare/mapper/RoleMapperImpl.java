package org.datn.petcare.mapper;

import javax.annotation.processing.Generated;
import org.datn.petcare.dto.RoleDTO;
import org.datn.petcare.entity.Role;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T21:25:40+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public Role toEntity(RoleDTO roleDTO) {
        if ( roleDTO == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.role( roleDTO.getRole() );

        return role.build();
    }
}
