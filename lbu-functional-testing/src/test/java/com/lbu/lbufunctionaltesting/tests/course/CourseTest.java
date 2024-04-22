package com.lbu.lbufunctionaltesting.tests.course;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.page.course.steps.CourseSteps;
import com.lbu.lbufunctionaltesting.tests.UserLevelBaseTest;
import org.junit.jupiter.api.Test;

public class CourseTest extends UserLevelBaseTest {

    @LazyAutowired
    CourseSteps courseSteps;

    @Test
    public void WhenCourseSearch_ThenFilter_ReturnFilteredList() {
        homeSteps
                .verifyThatIamAUser()
                .givenIAmAtAllCoursePage();
        courseSteps
                .givenIamTryingToSearchCourse();
    }

    @Test
    public void WhenCourseSearch_ThenFilterAndEnroll_ReturnSuccessMessage() {
        homeSteps
                .givenIAmAtAllCoursePage();
        courseSteps
                .givenIamTryingToSearchCourse()
                .givenIamTryingToEnrollCourse();
    }

}
