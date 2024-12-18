package org.datn.petcare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    private String password;
    private String fullName;
    @Lob
    private byte[] image;
    private boolean gender;
    private String email;
    private String phone;
    private String address;
    private boolean isDeleted;
    private boolean isRegistered;

    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role", referencedColumnName = "role")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Pet> pets;

    @OneToMany(mappedBy = "user")
    private List<BookedService> bookedServices;

    public void addPet(Pet pet) {
        pets.add(pet);
        pet.setUser(this);
    }
}
