import React, {useEffect, useState} from "react";
import {Box, Button, Divider, Grid, Paper, Typography} from "@mui/material";
import Layout from "../../layout/Layout";
import Letter from "../../components/letter/Letter";
import CourseSummery from "../../components/coursesummery/CourseSummery";
import StudentService from "../../services/student/StudentService";
import CourseService from "../../services/course/CourseService";
import {ICourses} from "../../services/course/CourseDto";
import {JwtDto} from "../../services/auth/AuthDto";
import Certificate from "../../components/certificate/Certificate";
import FinanceService from "../../services/finance/FinanceService";
import {InvoiceDetailDtos} from "../../services/finance/FinanceDto";

interface GraduationProps {
    jwtPayload: JwtDto;
}

// @ts-ignore
const Graduation = (prop: GraduationProps) => {
    const [courses, setCourses] = useState<ICourses>({
        courses: [
            {
                idHref: 's',
                title: 'asf',
                description: 'asf',
                fees: 0,
                durationInDays: 0,
                instructor: 'asf'
            }
        ]
    })
    let [invoiceList, setInvoices] = useState<InvoiceDetailDtos>()
    const [open, setOpen] = React.useState(false);
    const handleClose = () => {
        setOpen(false);
    };
    const handleToggle = () => {
        setOpen(!open);
    };

    const isGraduated = () => {
        let isGraduate = true;
        invoiceList?.invoiceList.forEach(value => {
            if (value.status === "OUTSTANDING") {
                isGraduate = false;
            }
        })
        return isGraduate;
    };

    useEffect(() => {
        handleToggle()
        const fetchData = async () => {
            try {
                const studentData = await StudentService.getStudentDetail();
                const courseData = await CourseService.getCoursesForList(studentData.courseHrefs.map(course => course.replace('/courses/', '')))
                const financeAccountData = await FinanceService.getFinanceAccount();
                setInvoices(financeAccountData);
                setCourses(courseData);
                handleClose();
            } catch (error) {
                // @ts-ignore
                if (error.message) {
                    // @ts-ignore
                    setError(error.message);
                }
                handleClose();
            }
        };
        fetchData();
        return () => {
        }
    }, []);

    return (
        <Layout>
            <Grid container spacing={1}>
                <Grid item xs={12}>
                    <Grid item>
                        <Typography sx={{paddingTop: 1, paddingLeft: 1}} variant="h4" id={"graduationTitle"}>Graduation Status</Typography>
                    </Grid>
                </Grid>
                <Grid item xs={12}>
                    {isGraduated() ?
                        <Certificate recipientName={prop.jwtPayload.firstName + " " + prop.jwtPayload.lastName}
                                     courseDuration={courses.courses
                                         .reduce((accumulator, currentValue) => accumulator + currentValue.durationInDays, 0)}/>
                        : <Letter isGraduated={isGraduated()}
                                  studentName={prop.jwtPayload.firstName + " " + prop.jwtPayload.lastName}/>}
                </Grid>
            </Grid>
        </Layout>
    );
};

export default Graduation;