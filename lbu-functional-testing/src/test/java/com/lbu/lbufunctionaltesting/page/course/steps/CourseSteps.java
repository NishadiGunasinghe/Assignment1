package com.lbu.lbufunctionaltesting.page.course.steps;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.course.AllCoursePage;

@LazyComponent
public class CourseSteps {

    @LazyAutowired
    AllCoursePage allCoursePage;

    public CourseSteps givenIamTryingToSearchCourse() {
        allCoursePage
                .searchCourseTitle()
                .verifyCourseTitle();
        return this;
    }

    public CourseSteps givenIamTryingToEnrollCourse() {
        allCoursePage
                .searchCourseTitle()
                .verifyCourseTitle()
                .enrollCourse();
        return this;
    }

    public CourseSteps givenIamTryingToCancel() {
        allCoursePage.closeSuccessEnroll();
        return this;
    }
}
