package org.datn.petcare.service.admin;

import org.datn.petcare.dto.GroupServiceDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GroupServiceService {
    List<GroupServiceDTO> getAll();

    Page<GroupServiceDTO> getByPage(int page, int size);

    GroupServiceDTO getById(int id);
    GroupServiceDTO create(GroupServiceDTO dto);
    GroupServiceDTO update(int id,GroupServiceDTO dto);
    void delete(int id);
}
