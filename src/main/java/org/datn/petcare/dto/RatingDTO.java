package org.datn.petcare.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingDTO {
    private int id; // ID của đánh giá
    private int rating; // Số sao
    private String comment; // Ý kiến
    private byte[] image; // Hình ảnh (nếu có)
    private LocalDateTime createdAt; // Thời gian tạo
    private int bookedServiceId;
    MultipartFile imageFile;
    private String base64Image;

    private String userFullName;
    private String userBase64Image;
    private byte[] userImage;
    private int groupServiceId;

    // Constructor đầy đủ cho tất cả các trường
    public RatingDTO(int id, int rating, String comment, byte[] image, LocalDateTime createdAt,
                     int bookedServiceId, String userFullName, byte[] userImage) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.image = image;
        this.createdAt = createdAt;
        this.bookedServiceId = bookedServiceId;
        this.userFullName = userFullName;
        this.userImage = userImage;
        this.base64Image = image != null ? "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(image) : null;
        this.userBase64Image = userImage != null ? "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(userImage) : null;
    }
    public RatingDTO(int id, int rating, String comment, byte[] image, LocalDateTime createdAt,
                     int bookedServiceId, String userFullName, byte[] userImage, int groupServiceId) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.image = image;
        this.createdAt = createdAt;
        this.bookedServiceId = bookedServiceId;
        this.userFullName = userFullName;
        this.userImage = userImage;
        this.groupServiceId = groupServiceId; // Đảm bảo khởi tạo groupServiceId
        this.base64Image = image != null ? "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(image) : null;
        this.userBase64Image = userImage != null ? "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(userImage) : null;
    }
}
