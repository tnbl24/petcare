package org.datn.petcare.service.admin;

import org.datn.petcare.dto.GroupServiceDTO;
import org.datn.petcare.dto.ServiceDetailDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ServiceDetailService {
    List<ServiceDetailDTO> getAll();
    ServiceDetailDTO create(ServiceDetailDTO serviceDetailDTO);
    ServiceDetailDTO update(int id,ServiceDetailDTO serviceDetailDTO);
    void delete(int id);

    Page<ServiceDetailDTO> getByPage(int page, int size);
}
