package org.datn.petcare.mapper;

import java.util.Arrays;
import javax.annotation.processing.Generated;
import org.datn.petcare.dto.PetDTO;
import org.datn.petcare.entity.Pet;
import org.datn.petcare.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T21:25:40+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class PetMapperImpl implements PetMapper {

    @Override
    public Pet toEntity(PetDTO petDTO) {
        if ( petDTO == null ) {
            return null;
        }

        Pet.PetBuilder pet = Pet.builder();

        pet.user( petDTOToUser( petDTO ) );
        pet.id( petDTO.getId() );
        pet.name( petDTO.getName() );
        pet.type( petDTO.getType() );
        pet.gender( petDTO.isGender() );
        pet.weight( petDTO.getWeight() );
        pet.age( petDTO.getAge() );
        byte[] image = petDTO.getImage();
        if ( image != null ) {
            pet.image( Arrays.copyOf( image, image.length ) );
        }
        pet.description( petDTO.getDescription() );

        return pet.build();
    }

    @Override
    public PetDTO toDTO(Pet pet) {
        if ( pet == null ) {
            return null;
        }

        PetDTO.PetDTOBuilder petDTO = PetDTO.builder();

        petDTO.userId( petUserId( pet ) );
        petDTO.id( pet.getId() );
        petDTO.name( pet.getName() );
        petDTO.type( pet.getType() );
        petDTO.gender( pet.isGender() );
        petDTO.weight( pet.getWeight() );
        petDTO.age( pet.getAge() );
        byte[] image = pet.getImage();
        if ( image != null ) {
            petDTO.image( Arrays.copyOf( image, image.length ) );
        }
        petDTO.description( pet.getDescription() );

        return petDTO.build();
    }

    protected User petDTOToUser(PetDTO petDTO) {
        if ( petDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( petDTO.getUserId() );

        return user.build();
    }

    private String petUserId(Pet pet) {
        if ( pet == null ) {
            return null;
        }
        User user = pet.getUser();
        if ( user == null ) {
            return null;
        }
        String id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
