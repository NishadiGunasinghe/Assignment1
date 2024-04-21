import {ICourses} from "../../services/course/CourseDto";
import {CardHeader, Paper, Typography} from "@mui/material";
import Card from "@mui/material/Card";
import React from "react";

interface CourseSummeryProp {
    courseData: ICourses
}

const CourseSummery = (prop: CourseSummeryProp) => {
    return (<Paper sx={{
        padding: 4,
        maxWidth: 600,
        margin: 'auto',
        marginTop: 4,
        textAlign: 'center',
    }}>
        <Typography  variant="h4" gutterBottom>
            Enrolled Courses
        </Typography>
        {prop.courseData.courses.map(value => {
            return <Card variant="outlined">
                <CardHeader title={value.title}
                            subheader={"Course duration : " + value?.durationInDays + " days"}
                />
            </Card>;
        })}
    </Paper>);
}

export default CourseSummery;