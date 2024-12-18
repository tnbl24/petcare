package org.datn.petcare.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetDTO {
    private int id;
    private String name;
    private String type;
    private boolean gender;
    private double weight;
    private int age;
    private byte[] image;
    private String description;

    private String userId;
}
