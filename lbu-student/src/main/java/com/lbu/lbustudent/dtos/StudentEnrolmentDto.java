package com.lbu.lbustudent.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Student Course Enrolments")
public class StudentEnrolmentDto {
    @Schema(description = "Auth User Href")
    private String authUserHref;
    @Schema(description = "Course Id Href")
    private String courseHref;
}
