package org.datn.petcare.service.admin.Impl;

import jakarta.persistence.criteria.*;
import org.datn.petcare.dto.RequestDTO;
import org.datn.petcare.dto.SearchRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilterSpecification<T>{

    public Specification<T> getSearchSpecification(SearchRequestDTO searchRequestDTO){
        return  new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(searchRequestDTO.getColumn()), searchRequestDTO.getValue());
            }
        };
    }

    public Specification<T> getSearchSpecification(List<SearchRequestDTO> searchRequestDTO, RequestDTO.GlobalOperator globalOperator) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Định dạng ngày

            for (SearchRequestDTO searchRequestDTO1 : searchRequestDTO) {
                if (searchRequestDTO1.getValue() != null) {
                    switch (searchRequestDTO1.getOperation()) {
                        case EQUAL -> {
                            Object value = searchRequestDTO1.getValue();

                            if (value instanceof LocalDate localDate) {
                                // Nếu là LocalDate, định dạng thành chuỗi
                                String dateString = localDate.format(formatter);
                                predicates.add(criteriaBuilder.equal(root.get(searchRequestDTO1.getColumn()), dateString));
                            } else if (value instanceof String dateString) {
                                // Nếu là String, chuyển đổi thành LocalDate
                                try {
                                    LocalDate localDateValue = LocalDate.parse(dateString, formatter);
                                    predicates.add(criteriaBuilder.equal(root.get(searchRequestDTO1.getColumn()), localDateValue));
                                } catch (DateTimeParseException e) {
                                    System.err.println("Định dạng ngày không hợp lệ: " + dateString);
                                    // Xử lý lỗi nếu cần
                                }
                            }
                        }
                        case LIKE -> {
                            predicates.add(criteriaBuilder.like(root.get(searchRequestDTO1.getColumn()), "%" + searchRequestDTO1.getValue() + "%"));
                        }
                        case BETWEEN -> {
                            String[] values = searchRequestDTO1.getValue().split(","); // Tách chuỗi bằng dấu phẩy
                            if (values.length == 2) {
                                try {
                                    // Chuyển đổi từ chuỗi sang LocalDate
                                    LocalDate startDate = LocalDate.parse(values[0].trim(), formatter);
                                    LocalDate endDate = LocalDate.parse(values[1].trim(), formatter);
                                    predicates.add(criteriaBuilder.between(root.get(searchRequestDTO1.getColumn()), startDate, endDate));
                                } catch (DateTimeParseException e) {
                                    System.err.println("Lỗi khi chuyển đổi ngày trong BETWEEN: " + e.getMessage());
                                    // Xử lý lỗi nếu cần
                                }
                            }
                        }
                        case JOIN -> {
                            predicates.add(criteriaBuilder.equal(root.join(searchRequestDTO1.getJoinTable()).get(searchRequestDTO1.getColumn()), searchRequestDTO1.getValue()));
                        }
                    }
                }
            }

            return globalOperator.equals(RequestDTO.GlobalOperator.AND)
                    ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                    : criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
