package org.datn.petcare.repository;

import org.datn.petcare.entity.ServiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetail, Integer> , JpaSpecificationExecutor<ServiceDetail> {
    boolean existsByWeight(String weight);
    List<ServiceDetail> findByServicesId(int serviceId);
}
