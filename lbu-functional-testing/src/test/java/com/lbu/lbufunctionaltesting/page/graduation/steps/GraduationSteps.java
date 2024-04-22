package com.lbu.lbufunctionaltesting.page.graduation.steps;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.graduation.GraduationPage;

@LazyComponent
public class GraduationSteps {

    @LazyAutowired
    GraduationPage graduationPage;


    public GraduationSteps verifyNeedCompletionOfPaymentBeforeCertificate() {
        graduationPage.verifyLetter();
        return this;
    }

    public GraduationSteps verifyCertificateIsThere() {
        graduationPage.verifyCertificate();
        return this;
    }
}
