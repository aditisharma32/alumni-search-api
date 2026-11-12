package com.example.alumnisearch.dto;

import com.example.alumnisearch.entity.AlumniProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlumniResponseDto {

    private String name;
    private String currentRole;
    private String university;
    private String location;
    private String linkedinHeadline;
    private Integer passoutYear;

    public static AlumniResponseDto fromEntity(AlumniProfile entity) {
        if (entity == null) {
            return null;
        }
        return AlumniResponseDto.builder()
                .name(entity.getName())
                .currentRole(entity.getCurrentRole())
                .university(entity.getUniversity())
                .location(entity.getLocation())
                .linkedinHeadline(entity.getLinkedinHeadline())
                .passoutYear(entity.getPassoutYear())
                .build();
    }
}
