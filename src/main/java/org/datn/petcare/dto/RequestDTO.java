package org.datn.petcare.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class RequestDTO {
    private List<SearchRequestDTO> searchRequestDTO;

    private GlobalOperator globalOperator;

//    private PageRequestDTO pageRequestDTO;

    public  enum GlobalOperator{
        AND,OR;
    }
}
