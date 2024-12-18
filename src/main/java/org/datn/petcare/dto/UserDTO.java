package org.datn.petcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.datn.petcare.entity.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private byte[] image;
    private MultipartFile imageFile;
    private String base64Img;

    private boolean gender;
    private String email;
    private String phone;
    private String address;
    private boolean isDeleted;
    private boolean isRegistered;

    private String role;

    private List<PetDTO> pets;
}
