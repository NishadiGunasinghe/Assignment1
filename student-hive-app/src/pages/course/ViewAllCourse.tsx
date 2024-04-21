import React, {useEffect, useState} from "react";
import Layout from "../../layout/Layout";
import {Backdrop, Box, CircularProgress, Grid, Typography} from "@mui/material";
import ViewCourse from "../../components/viewcourse/ViewCourse";
import AutoCompleteCourse from "../../components/autocompletecourse/AutoCompleteCourse";
import {ICourses} from "../../services/course/CourseDto";
import CourseService from "../../services/course/CourseService";
import ErrorDialog from "../../components/error/ErrorDialog";
import {IStudentDto} from "../../services/student/IStudentDto";
import StudentService from "../../services/student/StudentService";
import {JwtDto} from "../../services/auth/AuthDto";
import AuthService from "../../services/auth/AuthService";

interface ViewAllCourseProps {
    jwtPayload: JwtDto;
}

export default function ViewAllCourse(props: ViewAllCourseProps) {
    const [coursesData, setCourses] = useState<ICourses>();
    const [searchTitle, setSearchTitle] = useState('');

    const [message, setMessageToBody] = useState('');

    const [studentDetails, setStudentDetails] = useState<IStudentDto>({
        id: '',
        authUserHref: '',
        createdTimestamp: '',
        updatedTimestamp: '',
        courseHrefs: []
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
        handleToggle();
        const fetchData = async () => {
            try {
                const courseData = await CourseService.getAllCourses();
                setCourses(courseData);
                if (AuthService.checkIsAdminOrStudent(props.jwtPayload)) {
                    const studentData = await StudentService.getStudentDetail();
                    setStudentDetails(studentData);
                }
                handleClose();
            } catch (error) {
                // @ts-ignore
                if (error.message) {
                    // @ts-ignore
                    setError(error.message);
                }
                setMessageToBody("Apologetically, no courses are currently available. Please check back later for updates.")
                handleClose();
            }
        };

        fetchData();

        return () => {
        }
    }, []);

    const handleSearchTitle = (option: any) => {
        if (option) {
            setSearchTitle(option.title);
        } else {
            setSearchTitle('');
        }
    };

    if (coursesData) {
        // Filter the courses based on the searchTitle
        const filteredCourses = searchTitle
            ? coursesData.courses.filter((course) =>
                course.title.toLowerCase().includes(searchTitle.toLowerCase())
            )
            : coursesData.courses;


        return <Layout>
            <Box sx={{bgcolor: '#ffffff', padding: 2}}>
                <Grid container rowSpacing={0} columnSpacing={{xs: 0, sm: 0, md: 0}}>
                    <Grid xs={12}>
                        <AutoCompleteCourse courseData={coursesData} handleSearch={handleSearchTitle}/>
                    </Grid>
                    {filteredCourses.map(item => (
                        <Grid xs={3}>
                            <ViewCourse maxWidth={"100%"} courseData={item} textMaxLength={200} minHeight={100}
                                        studentData={studentDetails}/>
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
    } else {
        return <Layout>
            <Box sx={{bgcolor: '#ffffff', padding: 2}}>
                <Grid container rowSpacing={0} columnSpacing={{xs: 0, sm: 0, md: 0}}>
                    <Typography variant="body1" align="center" paragraph>
                        <span style={{color: "black"}}>{message}</span>{" "}
                    </Typography>
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

};