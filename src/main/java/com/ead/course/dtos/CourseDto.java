package com.ead.course.dtos;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CourseDto(
        @NotBlank
        String name,
        @NotBlank
        String description,
        String imageUrl,
        @NotNull
        CourseStatus courseStatus,
        @NotNull
        UUID userInstructor,
        @NotNull
        CourseLevel courseLevel
) {
}
