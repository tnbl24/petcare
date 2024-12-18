package org.datn.petcare.service.admin.Impl;

import jakarta.persistence.EntityNotFoundException;
import org.datn.petcare.dto.ServiceDTO;
import org.datn.petcare.entity.GroupService;
import org.datn.petcare.entity.ServiceDetail;
import org.datn.petcare.entity.Services;
import org.datn.petcare.mapper.ServicesMapper;
import org.datn.petcare.repository.GroupServiceRepository;
import org.datn.petcare.repository.ServiceDetailRepository;
import org.datn.petcare.repository.ServicesRepository;
import org.datn.petcare.service.admin.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicesImpl implements ServiceService {

    @Autowired
    ServicesMapper servicesMapper;

    @Autowired
    ServicesRepository servicesRepository;

    @Autowired
    GroupServiceRepository groupServiceRepository;

    @Autowired
    ServiceDetailRepository serviceDetailRepository;

    @Override
    public List<ServiceDTO> getAllActive() {
        List<Services> services = servicesRepository.findByIsDeleted(false);
        return services.stream()
                .map(servicesMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDTO> getAll() {
        List<Services> services = servicesRepository.findAll();
        return services.stream()
                .map(servicesMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceDTO create(ServiceDTO service) {
        Services existingService = servicesRepository.findByName(service.getName());

        if (existingService != null) {
            if (!existingService.isDeleted()) {
                throw new RuntimeException("Dịch vụ đã tồn tại");
            }

            existingService.setDeleted(false);

            if ("1".equals(service.getHasDetail())) {
                List<ServiceDetail> serviceDetails = serviceDetailRepository.findAll();
                existingService.setServiceDetails(serviceDetails);
            } else {
                existingService.setServiceDetails(new ArrayList<>());
            }

            existingService = servicesRepository.save(existingService);
            return servicesMapper.toDTO(existingService);
        } else {
            Services newService = servicesMapper.toEntity(service);
            newService.setDeleted(false);

            if (service.getImageFile() != null && !service.getImageFile().isEmpty()) {
                try {
                    byte[] imageBytes = service.getImageFile().getBytes();
                    newService.setImage(imageBytes);
                } catch (IOException e) {
                    throw new RuntimeException("Error uploading image: " + e.getMessage());
                }
            }

            if ("1".equals(service.getHasDetail())) {
                List<ServiceDetail> serviceDetails = serviceDetailRepository.findAll();
                newService.setServiceDetails(serviceDetails);
            } else {
                newService.setServiceDetails(new ArrayList<>());
            }

            newService = servicesRepository.save(newService);
            return servicesMapper.toDTO(newService);
        }
    }

    @Override
    public ServiceDTO update(int id, ServiceDTO serviceDTO) {
        Services existingService = servicesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service with ID " + id + " not found"));

        if (serviceDTO.getName() != null && !serviceDTO.getName().isEmpty()) {
            Services serviceWithSameName = servicesRepository.findByName(serviceDTO.getName());
            if (serviceWithSameName != null && serviceWithSameName.getId() != existingService.getId()) {
                throw new RuntimeException("Service name already exists. Please choose a different name.");
            }
            existingService.setName(serviceDTO.getName());
        }

        if (serviceDTO.getDescription() != null) {
            existingService.setDescription(serviceDTO.getDescription());
        }

        if (serviceDTO.getImageFile() != null && !serviceDTO.getImageFile().isEmpty()) {
            try {
                byte[] imageBytes = serviceDTO.getImageFile().getBytes();
                existingService.setImage(imageBytes);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading image: " + e.getMessage());
            }
        }

        if (serviceDTO.getPrice() >= 0.0) {
            existingService.setPrice(serviceDTO.getPrice());
        }

        existingService.setDeleted(serviceDTO.isDeleted());

        existingService.setHasDetail(serviceDTO.getHasDetail());

        if (serviceDTO.getGroupServiceId() > 0) {
            GroupService groupService = groupServiceRepository.findById(serviceDTO.getGroupServiceId())
                    .orElseThrow(() -> new RuntimeException("GroupService not found"));
            existingService.setGroupService(groupService);
        }

        if ("1".equals(serviceDTO.getHasDetail())) {
            List<ServiceDetail> serviceDetails = serviceDetailRepository.findAll();
            existingService.setServiceDetails(serviceDetails);
        } else {
            existingService.setServiceDetails(new ArrayList<>());
        }

        Services updatedService = servicesRepository.save(existingService);

        return servicesMapper.toDTO(updatedService);
    }

    @Override
    public void delete(int id) {
        Services service = servicesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service with ID " + id + " not found"));

        service.setDeleted(true);
        servicesRepository.save(service);
    }

    @Override
    public ServiceDTO getById(int id) {
        Services services = servicesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));

        return servicesMapper.toDTO(services);
    }

    @Override
    public Page<ServiceDTO> getActiveByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Services> servicesPage = servicesRepository.findByIsDeleted(false, pageable);

        return servicesPage.map(servicesMapper::toDTO);
    }

}
