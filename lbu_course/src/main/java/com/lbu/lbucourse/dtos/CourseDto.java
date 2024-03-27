package com.lbu.lbucourse.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Course detail")
public class CourseDto {
    @Schema(description = "href for the course id")
    private String idHref;
    @Schema(description = "course title")
    private String title;
    @Schema(description = "course description")
    private String description;
    @Schema(description = "course fees")
    private BigDecimal fees;
    @Schema(description = "course duration in days")
    private Integer durationInDays;
    @Schema(description = "course instructor")
    private String instructor;
}
