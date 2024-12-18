package org.datn.petcare.service.admin.Impl;

import org.datn.petcare.dto.GroupServiceDTO;
import org.datn.petcare.dto.ServiceDetailDTO;
import org.datn.petcare.entity.ServiceDetail;
import org.datn.petcare.entity.Services;
import org.datn.petcare.exception.ResourceNotFoundException;
import org.datn.petcare.mapper.ServiceDetailMapper;
import org.datn.petcare.repository.ServiceDetailRepository;
import org.datn.petcare.repository.ServicesRepository;
import org.datn.petcare.service.admin.ServiceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceDetailImpl implements ServiceDetailService {

    @Autowired
    private ServiceDetailMapper serviceDetailMapper;

    @Autowired
    private ServiceDetailRepository serviceDetailRepository;


    @Override
    public List<ServiceDetailDTO> getAll() {

        List<ServiceDetail> serviceDetails = serviceDetailRepository.findAll();

        return serviceDetails.stream()
                .map(serviceDetailMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceDetailDTO create(ServiceDetailDTO serviceDetailDTO) {

        if (serviceDetailRepository.existsByWeight(serviceDetailDTO.getWeight())) {
            throw new RuntimeException("Weight already exists");
        }

        ServiceDetail serviceDetail = serviceDetailMapper.toEntity(serviceDetailDTO);
        serviceDetail = serviceDetailRepository.save(serviceDetail);
        return serviceDetailMapper.toDTO(serviceDetail);
    }

    @Override
    public ServiceDetailDTO update(int id, ServiceDetailDTO serviceDetailDTO) {

        ServiceDetail serviceDetail = serviceDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service detail not found"));

        String newWeight = serviceDetailDTO.getWeight();

//        if (serviceDetailRepository.existsByWeight(newWeight) &&
//                newWeight != serviceDetail.getWeight()) {
//            throw new RuntimeException("Weight already exists");
//        }

        serviceDetail.setPrice(serviceDetailDTO.getPrice());
        serviceDetail.setWeight(newWeight);

        serviceDetail = serviceDetailRepository.save(serviceDetail);
        return serviceDetailMapper.toDTO(serviceDetail);
    }

    @Override
    public void delete(int id) {
        ServiceDetail serviceDetail = serviceDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceDetail not found"));

        for (Services service : serviceDetail.getServices()) {
            service.getServiceDetails().remove(serviceDetail);
        }

        serviceDetailRepository.deleteById(id);
    }

    @Override
    public Page<ServiceDetailDTO> getByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        return serviceDetailRepository.findAll(pageable).map(serviceDetailMapper::toDTO);
    }
}
