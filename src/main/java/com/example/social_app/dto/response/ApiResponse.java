package com.example.social_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"code", "message", "data"})
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T> {
    @Builder.Default
    int code = 100;
    @Builder.Default
    String message = "Successfully";
    T data;
}
