package org.datn.petcare.dto;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDTO {
    String column;
    String value;

    Operation operation;

    String joinTable;

    public enum Operation {
        EQUAL,
        LIKE,
        BETWEEN,
        JOIN
        ;
    }

    public SearchRequestDTO(String column, String value) {
        this.column = column;
        this.value = value;
        this.operation = Operation.EQUAL; // Giá trị mặc định
    }
}
