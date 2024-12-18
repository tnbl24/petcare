package org.datn.petcare.repository;

import org.datn.petcare.entity.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServicesRepository extends JpaRepository<Services, Integer> , JpaSpecificationExecutor<Services> {
    List<Services> findByIsDeleted(boolean isDeleted);
    Services findByName(String name);
    Page<Services> findByIsDeleted(boolean isDeleted, Pageable pageable);

    @Query("SELECT s FROM Services s WHERE " +
            "s.isDeleted = false AND " +
            "(:search IS NULL OR s.name LIKE %:search%) AND " +
            "(:groupServiceId IS NULL OR s.groupService.id = :groupServiceId) AND " +
            "(:priceFrom IS NULL OR s.price >= :priceFrom) AND " +
            "(:priceTo IS NULL OR s.price <= :priceTo)"
            + "ORDER BY s.name ASC"
    )
    Page<Services> findByCriteria(@Param("search") String search,
                                  @Param("groupServiceId") Integer groupServiceId,
                                  @Param("priceFrom") Double priceFrom,
                                  @Param("priceTo") Double priceTo,
                                  Pageable pageable);

    List<Services> findByGroupServiceId(Integer groupServiceId);

    @Query("SELECT s FROM Services s JOIN s.groupService g WHERE g.name <> 'combo' ORDER BY s.id DESC")
    List<Services> findTop5ByOrderByIdDesc(Pageable pageable);

    @Query("SELECT s FROM Services s JOIN s.groupService g WHERE g.name = 'combo' ORDER BY s.id DESC")
    List<Services> findAllComboServices();
}
