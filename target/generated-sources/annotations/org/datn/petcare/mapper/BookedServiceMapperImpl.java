package org.datn.petcare.mapper;

import javax.annotation.processing.Generated;
import org.datn.petcare.dto.BookedServiceDTO;
import org.datn.petcare.entity.BookedService;
import org.datn.petcare.entity.Pet;
import org.datn.petcare.entity.Rating;
import org.datn.petcare.entity.Services;
import org.datn.petcare.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T21:25:40+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class BookedServiceMapperImpl implements BookedServiceMapper {

    @Override
    public BookedService toEntity(BookedServiceDTO serviceDTO) {
        if ( serviceDTO == null ) {
            return null;
        }

        BookedService.BookedServiceBuilder bookedService = BookedService.builder();

        bookedService.user( bookedServiceDTOToUser( serviceDTO ) );
        bookedService.services( bookedServiceDTOToServices( serviceDTO ) );
        bookedService.pet( bookedServiceDTOToPet( serviceDTO ) );
        bookedService.rating( bookedServiceDTOToRating( serviceDTO ) );
        bookedService.id( serviceDTO.getId() );
        bookedService.bookingDate( serviceDTO.getBookingDate() );
        bookedService.bookingTime( serviceDTO.getBookingTime() );
        bookedService.note( serviceDTO.getNote() );
        bookedService.paymentMethod( serviceDTO.getPaymentMethod() );
        bookedService.price( serviceDTO.getPrice() );
        bookedService.status( serviceDTO.getStatus() );
        bookedService.createAt( serviceDTO.getCreateAt() );

        return bookedService.build();
    }

    @Override
    public BookedServiceDTO toDTO(BookedService service) {
        if ( service == null ) {
            return null;
        }

        BookedServiceDTO.BookedServiceDTOBuilder bookedServiceDTO = BookedServiceDTO.builder();

        bookedServiceDTO.userId( serviceUserId( service ) );
        bookedServiceDTO.serviceId( serviceServicesId( service ) );
        bookedServiceDTO.petId( servicePetId( service ) );
        bookedServiceDTO.ratingId( serviceRatingId( service ) );
        bookedServiceDTO.id( service.getId() );
        bookedServiceDTO.bookingDate( service.getBookingDate() );
        bookedServiceDTO.bookingTime( service.getBookingTime() );
        bookedServiceDTO.note( service.getNote() );
        bookedServiceDTO.paymentMethod( service.getPaymentMethod() );
        bookedServiceDTO.status( service.getStatus() );
        bookedServiceDTO.price( service.getPrice() );
        bookedServiceDTO.createAt( service.getCreateAt() );

        return bookedServiceDTO.build();
    }

    @Override
    public void updateEntityFromDTO(BookedServiceDTO serviceDTO, BookedService entity) {
        if ( serviceDTO == null ) {
            return;
        }

        entity.setBookingDate( serviceDTO.getBookingDate() );
        entity.setBookingTime( serviceDTO.getBookingTime() );
        entity.setNote( serviceDTO.getNote() );
        entity.setPaymentMethod( serviceDTO.getPaymentMethod() );
        entity.setPrice( serviceDTO.getPrice() );
        entity.setStatus( serviceDTO.getStatus() );
        entity.setCreateAt( serviceDTO.getCreateAt() );
    }

    protected User bookedServiceDTOToUser(BookedServiceDTO bookedServiceDTO) {
        if ( bookedServiceDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( bookedServiceDTO.getUserId() );

        return user.build();
    }

    protected Services bookedServiceDTOToServices(BookedServiceDTO bookedServiceDTO) {
        if ( bookedServiceDTO == null ) {
            return null;
        }

        Services.ServicesBuilder services = Services.builder();

        services.id( bookedServiceDTO.getServiceId() );

        return services.build();
    }

    protected Pet bookedServiceDTOToPet(BookedServiceDTO bookedServiceDTO) {
        if ( bookedServiceDTO == null ) {
            return null;
        }

        Pet.PetBuilder pet = Pet.builder();

        pet.id( bookedServiceDTO.getPetId() );

        return pet.build();
    }

    protected Rating bookedServiceDTOToRating(BookedServiceDTO bookedServiceDTO) {
        if ( bookedServiceDTO == null ) {
            return null;
        }

        Rating.RatingBuilder rating = Rating.builder();

        rating.id( bookedServiceDTO.getRatingId() );

        return rating.build();
    }

    private String serviceUserId(BookedService bookedService) {
        if ( bookedService == null ) {
            return null;
        }
        User user = bookedService.getUser();
        if ( user == null ) {
            return null;
        }
        String id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private int serviceServicesId(BookedService bookedService) {
        if ( bookedService == null ) {
            return 0;
        }
        Services services = bookedService.getServices();
        if ( services == null ) {
            return 0;
        }
        int id = services.getId();
        return id;
    }

    private int servicePetId(BookedService bookedService) {
        if ( bookedService == null ) {
            return 0;
        }
        Pet pet = bookedService.getPet();
        if ( pet == null ) {
            return 0;
        }
        int id = pet.getId();
        return id;
    }

    private int serviceRatingId(BookedService bookedService) {
        if ( bookedService == null ) {
            return 0;
        }
        Rating rating = bookedService.getRating();
        if ( rating == null ) {
            return 0;
        }
        int id = rating.getId();
        return id;
    }
}
