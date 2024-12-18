package org.datn.petcare.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogDTO {
    private int id;
    private String title;
    private byte[] image;
    private String base64Img;
    private MultipartFile imageFile;
    private String description;
    private String blogDetail;
}
