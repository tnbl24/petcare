package org.datn.petcare.service.admin.Impl;

import org.datn.petcare.dto.GroupServiceDTO;
import org.datn.petcare.entity.GroupService;
import org.datn.petcare.exception.ResourceNotFoundException;
import org.datn.petcare.mapper.GroupServiceMapper;
import org.datn.petcare.repository.GroupServiceRepository;
import org.datn.petcare.service.admin.GroupServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupServiceService {

    @Autowired
    GroupServiceRepository groupServiceRepository;

    @Autowired
    GroupServiceMapper groupServiceMapper;

    @Override
    public List<GroupServiceDTO> getAll() {
        List<GroupService> groupServices = groupServiceRepository.findAll();
        return groupServices.stream()
                .map(groupServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<GroupServiceDTO> getByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return groupServiceRepository.findAll(pageable).map(groupServiceMapper::toDto);
    }

    @Override
    public GroupServiceDTO getById(int id) {
            return groupServiceRepository.findById(id)
                    .map(groupServiceMapper::toDto)
                    .orElseThrow(() -> new ResourceNotFoundException("employee is not exists with given id " + id ));
    }

    @Override
    public GroupServiceDTO create(GroupServiceDTO dto) {

        if (groupServiceRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Group service already exists");
        }

        GroupService groupService = groupServiceMapper.toEntity(dto);
        groupService = groupServiceRepository.save(groupService);

        return groupServiceMapper.toDto(groupService);
    }

    @Override
    public GroupServiceDTO update(int id, GroupServiceDTO dto) {

        GroupService groupService = groupServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GroupService not found"));

        if (groupServiceRepository.existsByName(dto.getName())) {
            if (!groupService.getName().equals(dto.getName())) {
                throw new RuntimeException("Name already exists");
            }
        }

        groupService.setName(dto.getName());
        groupService = groupServiceRepository.save(groupService);

        return groupServiceMapper.toDto(groupService);
    }

    @Override
    public void delete(int id) {
        groupServiceRepository.deleteById(id);
    }
}
