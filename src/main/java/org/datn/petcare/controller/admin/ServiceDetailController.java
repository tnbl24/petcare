package org.datn.petcare.controller.admin;

import org.datn.petcare.dto.PageResponse;
import org.datn.petcare.dto.RequestDTO;
import org.datn.petcare.dto.ServiceDetailDTO;
import org.datn.petcare.entity.ServiceDetail;
import org.datn.petcare.mapper.ServiceDetailMapper;
import org.datn.petcare.repository.ServiceDetailRepository;
import org.datn.petcare.service.admin.Impl.FilterSpecification;
import org.datn.petcare.service.admin.ServiceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/service-detail-ctrl")
public class ServiceDetailController {

    @Autowired
    private ServiceDetailService serviceDetailService;

    @Autowired
    private ServiceDetailMapper serviceDetailMapper;

    @Autowired
    private ServiceDetailRepository serviceDetailRepository;

    @Autowired
    private FilterSpecification<ServiceDetail> filterSpecification;

    @GetMapping
    public ResponseEntity<List<ServiceDetailDTO>> serviceDetail() {
        List<ServiceDetailDTO> serviceDetailDTOS = serviceDetailService.getAll();
        return ResponseEntity.ok(serviceDetailDTOS);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<PageResponse<ServiceDetailDTO>> getByPage(
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ServiceDetailDTO> result = serviceDetailService.getByPage(page, size);
        PageResponse<ServiceDetailDTO> response = new PageResponse<>(result.getContent(), result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ServiceDetailDTO> create(@RequestBody ServiceDetailDTO serviceDetailDTO) {
        ServiceDetailDTO serviceDetailDTOSaved = serviceDetailService.create(serviceDetailDTO);
        return ResponseEntity.ok(serviceDetailDTOSaved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDetailDTO> update(@PathVariable int id, @RequestBody ServiceDetailDTO serviceDetailDTO) {
        ServiceDetailDTO serviceDetailDTOSaved = serviceDetailService.update(id, serviceDetailDTO);
        return ResponseEntity.ok(serviceDetailDTOSaved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServiceDetail(@PathVariable("id") int id) {
        serviceDetailService.delete(id);
        return ResponseEntity.ok("Deleted service detail");
    }

    @PostMapping("/filter/page/{page}")
    public ResponseEntity<PageResponse<ServiceDetailDTO>> filterWithPagination(
            @RequestBody RequestDTO requestDTO,
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {

        // Tạo Specification từ requestDTO
        Specification<ServiceDetail> specification = filterSpecification.getSearchSpecification(
                requestDTO.getSearchRequestDTO(),
                requestDTO.getGlobalOperator()
        );

        // Tạo PageRequest với sắp xếp theo cân nặng (weight)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("price").ascending());

        // Lấy kết quả từ repository
        Page<ServiceDetail> result = serviceDetailRepository.findAll(specification, pageRequest);

        // Chuyển đổi kết quả thành DTO
        PageResponse<ServiceDetailDTO> response = new PageResponse<>(
                result.getContent().stream()
                        .map(serviceDetailMapper::toDTO)
                        .collect(Collectors.toList()),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

}
