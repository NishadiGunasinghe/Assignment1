import * as React from 'react';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import {styled} from '@mui/system';
import {ICourses} from "../../services/course/CourseDto";

export interface AutoCompleteCourseProps {
    courseData: ICourses,
    handleSearch: any;
}

const GroupHeader = styled('div')(({theme}) => ({
    position: 'sticky',
    top: '-8px',
    padding: '4px 10px'
}));

const GroupItems = styled('ul')({
    padding: 0,
});

export default function AutoCompleteCourse(props: AutoCompleteCourseProps) {
    const options = props.courseData.courses.map((option) => {
        const firstLetter = option.title[0].toUpperCase();
        return {
            firstLetter: /[0-9]/.test(firstLetter) ? '0-9' : firstLetter,
            ...option,
        };
    });

    const handleOptionChange = (_: any, option: any) => {
            props.handleSearch(option);
    };

    return (
        <Autocomplete
            id="courseAutoCompleteSearch"
            onChange={handleOptionChange}
            options={options.sort((a, b) => -b.firstLetter.localeCompare(a.firstLetter))}
            groupBy={(option) => option.firstLetter}
            getOptionLabel={(option) => option.title}
            sx={{minWidth: "100%"}}
            clearOnBlur={false}
            renderInput={(params) => <TextField {...params} label="Search courses"/>}
            renderGroup={(params) => (
                <li key={params.key}>
                    <GroupHeader>{params.group}</GroupHeader>
                    <GroupItems>{params.children}</GroupItems>
                </li>
            )}
        />
    );
}