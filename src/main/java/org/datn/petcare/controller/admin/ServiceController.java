package org.datn.petcare.controller.admin;

import org.datn.petcare.dto.*;
import org.datn.petcare.entity.Services;
import org.datn.petcare.mapper.ServicesMapper;
import org.datn.petcare.repository.ServicesRepository;
import org.datn.petcare.service.admin.Impl.FilterSpecification;
import org.datn.petcare.service.admin.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/service-ctrl")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ServicesRepository servicesRepository;

    @Autowired
    private ServicesMapper servicesMapper;

    @Autowired
    private FilterSpecification<Services> filterSpecification;

    @GetMapping("/all")
    public ResponseEntity<List<ServiceDTO>> serviceGetAll() {
        List<ServiceDTO> serviceDTOS = serviceService.getAll();
        return ResponseEntity.ok(serviceDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> serviceGetById(@PathVariable int id) {
        ServiceDTO serviceDTO = serviceService.getById(id);
        return ResponseEntity.ok(serviceDTO);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ServiceDTO>> serviceGetActive() {
        List<ServiceDTO> serviceDTOS = serviceService.getAllActive();
        return ResponseEntity.ok(serviceDTOS);
    }

    @GetMapping("/active/page/{page}")
    public ResponseEntity<PageResponse<ServiceDTO>> getByPage(
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {
            Page<ServiceDTO> result = serviceService.getActiveByPage(page, size);
            PageResponse<ServiceDTO> response = new PageResponse<>(result.getContent(), result.getTotalPages());
            return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ServiceDTO> createService(@ModelAttribute ServiceDTO serviceDTO) {
        ServiceDTO createService = serviceService.create(serviceDTO);
        return new ResponseEntity<>(createService, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable int id,
            @ModelAttribute ServiceDTO serviceDTO) {

        ServiceDTO updatedService = serviceService.update(id, serviceDTO);
        return new ResponseEntity<>(updatedService, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    String deleteService(@PathVariable("id") int id) {
        serviceService.delete(id);
        return "Deleted Service";
    }

    @PostMapping("/filter/page/{page}")
    public ResponseEntity<PageResponse<ServiceDTO>> filterWithPagination(
            @RequestBody RequestDTO requestDTO,
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {

        // Tạo Specification từ requestDTO
        Specification<Services> specification = filterSpecification.getSearchSpecification(
                requestDTO.getSearchRequestDTO(),
                requestDTO.getGlobalOperator()
        );

        // Tạo Specification để lọc các dịch vụ không bị xóa
        Specification<Services> isNotDeletedSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isDeleted"), false);

        // Kết hợp các Specification
        Specification<Services> combinedSpecification = specification.and(isNotDeletedSpecification);

        // Tạo PageRequest với sắp xếp theo tên dịch vụ
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());

        // Lấy kết quả từ repository
        Page<Services> result = servicesRepository.findAll(combinedSpecification, pageRequest);

        // Chuyển đổi kết quả thành DTO
        PageResponse<ServiceDTO> response = new PageResponse<>(
                result.getContent().stream()
                        .map(servicesMapper::toDTO)
                        .collect(Collectors.toList()),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}
