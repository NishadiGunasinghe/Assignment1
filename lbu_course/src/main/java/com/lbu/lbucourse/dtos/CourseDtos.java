package com.lbu.lbucourse.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Courses details list")
public class CourseDtos {
    @Schema(description = "Courses details list")
    List<CourseDto> courses;
}
