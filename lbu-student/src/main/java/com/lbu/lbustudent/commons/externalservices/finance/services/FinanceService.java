package com.lbu.lbustudent.commons.externalservices.finance.services;

import com.lbu.lbustudent.dtos.course.CourseDto;

public interface FinanceService {

    void createOrUpdateFinanceAccount(CourseDto courseDto, String authUserHref, String token);
}
