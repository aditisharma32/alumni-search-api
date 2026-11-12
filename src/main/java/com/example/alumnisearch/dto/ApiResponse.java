package com.example.alumnisearch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String status;
    private T data;
    private String message;
    private List<String> errors;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .errors(errors)
                .build();
    }
}
