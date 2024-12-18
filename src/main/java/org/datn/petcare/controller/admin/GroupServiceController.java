package org.datn.petcare.controller.admin;

import org.datn.petcare.dto.GroupServiceDTO;
import org.datn.petcare.dto.PageResponse;
import org.datn.petcare.dto.RequestDTO;
import org.datn.petcare.entity.GroupService;
import org.datn.petcare.mapper.GroupServiceMapper;
import org.datn.petcare.repository.GroupServiceRepository;
import org.datn.petcare.service.admin.GroupServiceService;
import org.datn.petcare.service.admin.Impl.FilterSpecification;
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
@RequestMapping("/admin/group-service-ctrl")
public class GroupServiceController {

    @Autowired
    private GroupServiceService groupService;

    @Autowired
    private GroupServiceMapper groupServiceMapper;

    @Autowired
    private GroupServiceRepository groupServiceRepository;

    @Autowired
    private FilterSpecification<GroupService> filterSpecification;

    @GetMapping()
    public ResponseEntity<List<GroupServiceDTO>> groupService() {
        List<GroupServiceDTO> groupServices = groupService.getAll();
        return ResponseEntity.ok(groupServices);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<PageResponse<GroupServiceDTO>> getByPage(
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GroupServiceDTO> result = groupService.getByPage(page, size);
        PageResponse<GroupServiceDTO> response = new PageResponse<>(result.getContent(), result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupServiceDTO> groupServiceById(@PathVariable("id") int id) {
        GroupServiceDTO groupServiceDTO = groupService.getById(id);
        return ResponseEntity.ok(groupServiceDTO);
    }

    @PostMapping
    public ResponseEntity<GroupServiceDTO> createGroupService(@RequestBody GroupServiceDTO groupServiceDTO) {
        GroupServiceDTO createdGroupService = groupService.create(groupServiceDTO);
        return new ResponseEntity<>(createdGroupService, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<GroupServiceDTO> updateGroupService(@PathVariable("id") int id, @RequestBody GroupServiceDTO groupServiceDTO) {
        GroupServiceDTO updatedGroupService = groupService.update(id, groupServiceDTO);
        return new ResponseEntity<>(updatedGroupService, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    String deleteGroupService(@PathVariable("id") int id) {
        groupService.delete(id);
        return "Deleted group service";
    }

    @PostMapping("/filter/page/{page}")
    public ResponseEntity<PageResponse<GroupServiceDTO>> filterWithPagination(
            @RequestBody RequestDTO requestDTO,
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {

        Specification<GroupService> specification = filterSpecification.getSearchSpecification(
                requestDTO.getSearchRequestDTO(),
                requestDTO.getGlobalOperator()
        );

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<GroupService> result = groupServiceRepository.findAll(specification, pageRequest);

        PageResponse<GroupServiceDTO> response = new PageResponse<>(
                result.getContent().stream()
                        .map(groupServiceMapper::toDto)
                        .collect(Collectors.toList()),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}
