package org.datn.petcare.controller.user;

import org.datn.petcare.dto.PetDTO;
import org.datn.petcare.service.user.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/pet-ctrl")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPetsById(@PathVariable int id) {
        PetDTO petDTO = petService.getById(id);
        return ResponseEntity.ok(petDTO);
    }

    @PostMapping
    public ResponseEntity<PetDTO> createPet(@RequestBody PetDTO petDTO) {
        PetDTO petCreate = petService.create(petDTO);
        return ResponseEntity.ok(petCreate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> updatePet(@PathVariable int id, @RequestBody PetDTO petDTO) {
        PetDTO petUpdate = petService.update(id, petDTO);
        return ResponseEntity.ok(petUpdate);
    }

    @DeleteMapping("/{id}")
    public String deletePet(@PathVariable int id) {
        petService.delete(id);
        return "Pet deleted";
    }
}
