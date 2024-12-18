package org.datn.petcare.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.processing.Generated;
import org.datn.petcare.dto.PetDTO;
import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.entity.Pet;
import org.datn.petcare.entity.Role;
import org.datn.petcare.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T21:25:40+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.role( userRoleRole( user ) );
        userDTO.id( user.getId() );
        userDTO.username( user.getUsername() );
        userDTO.password( user.getPassword() );
        userDTO.fullName( user.getFullName() );
        byte[] image = user.getImage();
        if ( image != null ) {
            userDTO.image( Arrays.copyOf( image, image.length ) );
        }
        userDTO.gender( user.isGender() );
        userDTO.email( user.getEmail() );
        userDTO.phone( user.getPhone() );
        userDTO.address( user.getAddress() );
        userDTO.pets( petListToPetDTOList( user.getPets() ) );

        return userDTO.build();
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.role( userDTOToRole( userDTO ) );
        user.id( userDTO.getId() );
        user.username( userDTO.getUsername() );
        user.password( userDTO.getPassword() );
        user.fullName( userDTO.getFullName() );
        byte[] image = userDTO.getImage();
        if ( image != null ) {
            user.image( Arrays.copyOf( image, image.length ) );
        }
        user.gender( userDTO.isGender() );
        user.email( userDTO.getEmail() );
        user.phone( userDTO.getPhone() );
        user.address( userDTO.getAddress() );
        user.pets( petDTOListToPetList( userDTO.getPets() ) );

        return user.build();
    }

    private String userRoleRole(User user) {
        if ( user == null ) {
            return null;
        }
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        String role1 = role.getRole();
        if ( role1 == null ) {
            return null;
        }
        return role1;
    }

    protected PetDTO petToPetDTO(Pet pet) {
        if ( pet == null ) {
            return null;
        }

        PetDTO.PetDTOBuilder petDTO = PetDTO.builder();

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

    protected List<PetDTO> petListToPetDTOList(List<Pet> list) {
        if ( list == null ) {
            return null;
        }

        List<PetDTO> list1 = new ArrayList<PetDTO>( list.size() );
        for ( Pet pet : list ) {
            list1.add( petToPetDTO( pet ) );
        }

        return list1;
    }

    protected Role userDTOToRole(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.role( userDTO.getRole() );

        return role.build();
    }

    protected Pet petDTOToPet(PetDTO petDTO) {
        if ( petDTO == null ) {
            return null;
        }

        Pet.PetBuilder pet = Pet.builder();

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

    protected List<Pet> petDTOListToPetList(List<PetDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Pet> list1 = new ArrayList<Pet>( list.size() );
        for ( PetDTO petDTO : list ) {
            list1.add( petDTOToPet( petDTO ) );
        }

        return list1;
    }
}
