package org.datn.petcare.service.admin;

import org.datn.petcare.dto.BookedServiceDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookedServiceService {
    List<BookedServiceDTO> getAll();
    BookedServiceDTO update(int id, BookedServiceDTO bookedServiceDTO);

    Page<BookedServiceDTO> getByPage(int page, int size);

}
