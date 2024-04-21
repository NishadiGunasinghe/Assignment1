import React, {useEffect, useState} from "react";
import Layout from "../../layout/Layout";
import {Backdrop, Box, CardHeader, CircularProgress, Divider, Grid, Typography} from "@mui/material";
import ErrorDialog from "../../components/error/ErrorDialog";
import CardContent from "@mui/material/CardContent";
import Card from "@mui/material/Card";
import {ICourses} from "../../services/course/CourseDto";
import StudentService from "../../services/student/StudentService";
import CourseService from "../../services/course/CourseService";

// @ts-ignore
export default function EnrolledCourses({ jwtPayload:JwtDto }) {
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

    const [error, setError] = useState<string | null>(null);
    const handleCloseErrorDialog = () => {
        setError(null);
    };

    const [open, setOpen] = React.useState(false);
    const handleClose = () => {
        setOpen(false);
    };
    const handleToggle = () => {
        setOpen(!open);
    };

    useEffect(() => {
        handleToggle()
        const fetchData = async () => {
            try {
                const studentData = await StudentService.getStudentDetail();
                const courseData = await CourseService.getCoursesForList(studentData.courseHrefs.map(course => course.replace('/courses/', '')))
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

    return <Layout>
        <Box sx={{bgcolor: '#ffffff', padding: 2}}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Grid item>
                        <Typography variant="h4">Enrolled Courses</Typography>
                    </Grid>
                    <Divider sx={{
                        marginTop: '25px', // Adjust the top margin as needed
                        marginBottom: '25px', // Adjust the bottom margin as needed
                    }}/>
                </Grid>
                {courses.courses.map(course => (
                    <Grid key={course.idHref} item xs={12} sm={6} md={4}>
                        <Card variant="outlined">
                            <CardHeader
                                title={course.title}
                                subheader={"Instructor: " + course.instructor}
                            />
                            <CardContent>
                                <Typography sx={{mb: 1.5}} color="text.secondary">
                                    Course duration : {course?.durationInDays} days
                                </Typography>
                                <Typography sx={{mb: 1.5}} color="text.secondary">
                                    Course Fee : {course?.fees} £
                                </Typography>
                                <Typography variant="body2">
                                    {course.description}
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                ))}
            </Grid>


        </Box>
        <ErrorDialog open={!!error} onClose={handleCloseErrorDialog} message={error || ''}/>
        <Backdrop
            sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
            open={open}
            onClick={handleClose}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </Layout>
}