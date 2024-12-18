package org.datn.petcare.service.user.Impl;

import org.datn.petcare.dto.PetDTO;
import org.datn.petcare.entity.Pet;
import org.datn.petcare.entity.User;
import org.datn.petcare.mapper.PetMapper;
import org.datn.petcare.repository.PetRepository;
import org.datn.petcare.repository.UserRepository;
import org.datn.petcare.service.user.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class PetImpl implements PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<PetDTO> getAllByUserId(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        List<Pet> pets = petRepository.findByUser(user);

        return pets.stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PetDTO getById(int id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + id));
        return petMapper.toDTO(pet);
    }

    @Override
    public PetDTO create(PetDTO petDTO) {
        Pet pet = petMapper.toEntity(petDTO);
        Pet savedPet = petRepository.save(pet);
        return petMapper.toDTO(savedPet);
    }

    @Override
    public PetDTO update(int id, PetDTO petDTO) {

        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found."));

        existingPet.setName(petDTO.getName());
        existingPet.setType(petDTO.getType());
        existingPet.setGender(petDTO.isGender());
        existingPet.setWeight(petDTO.getWeight());
        existingPet.setAge(petDTO.getAge());
        existingPet.setImage(petDTO.getImage());
        existingPet.setDescription(petDTO.getDescription());

        Pet updatedPet = petRepository.save(existingPet);
        return petMapper.toDTO(updatedPet);
    }

    @Override
    public void delete(int id) {
        if (!petRepository.existsById(id)) {
            throw new RuntimeException("Pet not found.");
        }
        petRepository.deleteById(id);
    }
}
