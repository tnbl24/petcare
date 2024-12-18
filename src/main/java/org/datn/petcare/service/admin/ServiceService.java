package org.datn.petcare.service.admin;

import org.datn.petcare.dto.ServiceDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ServiceService {
    List<ServiceDTO> getAllActive();
    List<ServiceDTO> getAll();
    ServiceDTO create(ServiceDTO service);
    ServiceDTO update(int id,ServiceDTO service);
    void delete(int id);

    ServiceDTO getById(int id);

    Page<ServiceDTO> getActiveByPage(int page, int size);
}
