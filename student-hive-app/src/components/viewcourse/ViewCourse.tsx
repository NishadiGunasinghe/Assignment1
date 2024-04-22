import * as React from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import {Backdrop, CardHeader, CircularProgress} from "@mui/material";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Dialog from "@mui/material/Dialog";
import {ICourse} from "../../services/course/CourseDto";
import {IStudentDto} from "../../services/student/IStudentDto";
import StudentService from "../../services/student/StudentService";
import {useState} from "react";
import ErrorDialog from "../error/ErrorDialog";
import SuccessDialog from "../error/SuccessDialog";

export interface ViewCourseProps {
    maxWidth?: string,
    courseData: ICourse,
    minHeight?: number,
    textMaxLength: number
    studentData?: IStudentDto
}

export default function ViewCourse(props: ViewCourseProps) {

    const [open, setOpen] = React.useState(false);

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const [error, setError] = useState<string | null>(null);
    const handleCloseErrorDialog = () => {
        setError(null);
    };

    const [openSpinner, setOpenSpinner] = React.useState(false);
    const handleSpinnerClose = () => {
        setOpenSpinner(false);
    };
    const handleToggle = () => {
        setOpenSpinner(!open);
    };


    const [success, setSuccessMessage] = React.useState<string | null>(null);
    const handleSuccessDialog = () => {
        setSuccessMessage(null);
        window.location.reload();
    }
    const handleEnroll = () => {
        handleToggle();
        const fetchData = async () => {
            try {
                const courseData = await StudentService.enrollCourses(props.courseData.idHref);
                handleClose();
                handleSpinnerClose();
                setSuccessMessage("Successfully enrolled for the subject : " + props.courseData.title);
                if (courseData.jwtTokenDto) {
                    if (courseData.jwtTokenDto.jwtToken) {
                        localStorage.setItem('token', courseData.jwtTokenDto.jwtToken);
                    }
                }
            } catch (error) {
                // @ts-ignore
                if (error.message) {
                    // @ts-ignore
                    setError(error.message);
                }
                handleClose();
                handleSpinnerClose();
            }
        };
        fetchData();
    };

    const checkIsDisabled = (courseHref: string) => {
        if (props.studentData) {
            if (props.studentData?.courseHrefs && props.studentData?.courseHrefs.length > 0) {
                return !!props.studentData?.courseHrefs.includes(courseHref);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    return (
        <div style={{marginTop: 10}}>
            <div style={{padding: 5}}>
                <Box sx={{maxWidth: props.maxWidth, padding: 0, minHeight: props.minHeight}}>
                    <Card variant="outlined" sx={{minHeight: props.minHeight}}>
                        <CardHeader
                            title={props.courseData.title}
                            subheader={"Instructor: " + props.courseData.instructor}
                        />
                        <CardContent sx={{minHeight: props.minHeight}}>
                            <Typography sx={{mb: 1.5}} color="text.secondary">
                                Course duration : {props.courseData?.durationInDays} days
                            </Typography>
                            <Typography sx={{mb: 1.5}} color="text.secondary">
                                Course Fee : {props.courseData?.fees} £
                            </Typography>
                            <Typography variant="body2">
                                {props.courseData.description.length > props.textMaxLength ?
                                    `${props.courseData.description.substring(0, props.textMaxLength)}...` : props.courseData.description}
                            </Typography>
                        </CardContent>
                        <CardActions>
                            <Button size="small" variant="outlined" onClick={handleClickOpen}
                                    disabled={checkIsDisabled(props.courseData.idHref)}>View More</Button>
                        </CardActions>

                    </Card>
                </Box>
            </div>

            <Dialog
                open={open}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">{props.courseData.title}</DialogTitle>
                <DialogContent>
                    <Typography sx={{mb: 1.5}} color="text.secondary">
                        Course duration : {props.courseData?.durationInDays} days
                    </Typography>
                    <Typography sx={{mb: 1.5}} color="text.secondary">
                        Course Fee : {props.courseData?.fees} £
                    </Typography>
                    <Typography variant="body2">
                        {props.courseData.description}
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button id={"enrollBtn"} onClick={handleEnroll}>Enroll</Button>
                    <Button onClick={handleClose} autoFocus>Cancel</Button>
                </DialogActions>
            </Dialog>

            <ErrorDialog open={!!error} onClose={handleCloseErrorDialog} message={error || ''}/>
            <SuccessDialog open={!!success} onClose={handleSuccessDialog} message={success || ''} id={"enroll"}/>
            <Backdrop
                sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
                open={openSpinner}
                onClick={handleSpinnerClose}
            >
                <CircularProgress color="inherit"/>
            </Backdrop>
        </div>
    );
}