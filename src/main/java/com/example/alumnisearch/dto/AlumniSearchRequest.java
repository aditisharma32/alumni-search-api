package com.example.alumnisearch.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniSearchRequest {

    @NotBlank(message = "University name cannot be blank")
    private String university;

    @NotBlank(message = "Designation cannot be blank")
    private String designation;

    private Integer passoutYear;

    @JsonSetter("passoutYear")
    public void setPassoutYearFlexibly(Object value) {
        if (value == null) {
            this.passoutYear = null;
        } else if (value instanceof Number num) {
            this.passoutYear = num.intValue();
        } else if (value instanceof String str) {
            str = str.trim();
            if (str.isEmpty() || str.equalsIgnoreCase("OPTIONAL_PASSOUT_YEAR") || str.equalsIgnoreCase("null")) {
                this.passoutYear = null;
            } else {
                try {
                    this.passoutYear = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    this.passoutYear = null;
                }
            }
        }
    }
}
