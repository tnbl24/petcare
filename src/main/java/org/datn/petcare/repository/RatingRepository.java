package org.datn.petcare.repository;

import org.datn.petcare.dto.RatingDTO;
import org.datn.petcare.entity.BookedService;
import org.datn.petcare.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByBookedServiceIn(List<BookedService> bookedServices);
    boolean existsByBookedServiceId(Integer bookedServiceId);
    Rating findByBookedServiceId(Integer bookedServiceId);

    @Query("SELECT new org.datn.petcare.dto.RatingDTO(r.id, r.rating, r.comment, r.image, r.createdAt, b.id, u.fullName, u.image) " +
            "FROM Rating r " +
            "JOIN r.bookedService b " +
            "JOIN b.user u " +
            "ORDER BY r.createdAt DESC")
    List<RatingDTO> findAllWithUserInfo();

    @Query("SELECT new org.datn.petcare.dto.RatingDTO(r.id, r.rating, r.comment, r.image, r.createdAt, " +
            "b.id, u.fullName, u.image, s.groupService.id) " +
            "FROM Rating r " +
            "JOIN r.bookedService b " +
            "JOIN b.user u " +
            "JOIN b.services s " +
            "ORDER BY r.createdAt DESC")
    List<RatingDTO> findAllByGroupServiceId(@Param("groupServiceId") Integer groupServiceId);
}
