package org.datn.petcare.controller.admin;

import org.datn.petcare.dto.BookedServiceDTO;
import org.datn.petcare.dto.PageResponse;
import org.datn.petcare.dto.RequestDTO;
import org.datn.petcare.entity.BookedService;
import org.datn.petcare.mapper.BookedServiceMapper;
import org.datn.petcare.repository.BookedServiceRepository;
import org.datn.petcare.service.admin.BookedServiceService;
import org.datn.petcare.service.admin.Impl.FilterSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/booked-service-ctrl")
public class BookedServiceController {

    @Autowired
    private BookedServiceService bookedService;

    @Autowired
    private BookedServiceRepository bookedServiceRepository;

    @Autowired
    private BookedServiceMapper bookedServiceMapper;

    @Autowired
    private FilterSpecification<BookedService> filterSpecification;

    @GetMapping("/all")
    public ResponseEntity<List<BookedServiceDTO>> getAllBookedServices() {
        List<BookedServiceDTO> bookedServices = bookedService.getAll();
        return ResponseEntity.ok(bookedServices);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookedServiceDTO> updateBookedService(@PathVariable int id, @RequestBody BookedServiceDTO bookedServiceDTO) {
        BookedServiceDTO bookedService2 = bookedService.update(id, bookedServiceDTO);
        return ResponseEntity.ok(bookedService2);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<PageResponse<BookedServiceDTO>> getByPage(
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookedServiceDTO> result = bookedService.getByPage(page, size);
        PageResponse<BookedServiceDTO> response = new PageResponse<>(result.getContent(), result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filter/page/{page}")
    public ResponseEntity<PageResponse<BookedServiceDTO>> filterWithPagination(
            @RequestBody RequestDTO requestDTO,
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {

        Specification<BookedService> specification = filterSpecification.getSearchSpecification(
                requestDTO.getSearchRequestDTO(),
                requestDTO.getGlobalOperator()
        );

        Sort sort = Sort.by(Sort.Direction.DESC, "createAt"); // Sắp xếp tăng dần theo createAt
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<BookedService> result = bookedServiceRepository.findAll(specification, pageRequest);

        PageResponse<BookedServiceDTO> response = new PageResponse<>(
                result.getContent().stream()
                        .map(bookedServiceMapper::toDTO)
                        .toList(),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}
