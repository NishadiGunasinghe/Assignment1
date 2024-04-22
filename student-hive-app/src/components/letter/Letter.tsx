import {Button, Paper, Typography} from "@mui/material";
import React from "react";

let graduationPendingLetter: string[] = [
    "We hope this message finds you well. Thank you for your interest in checking your graduation status.",
    "Upon review of your records, we regret to inform you that there appears to be pending finance associated with your account."
];

interface LetterProp {
    isGraduated: boolean;
    studentName: string;
}

const Letter = (prop: LetterProp) => {
    return (
        <Paper sx={{
            padding: 4,
            maxWidth: 600,
            margin: 'auto',
            marginTop: 4,
            textAlign: 'center',
        }}>
            <Typography variant="h4" gutterBottom id={"graduationLetterTitle"}>
                Graduation Status Letter
            </Typography>
            <Typography variant="body1" sx={{
                textAlign: 'left',
                marginTop: 5,
            }}>
                Dear {prop.studentName},
            </Typography>

            {graduationPendingLetter.map(value => {
                return <Typography variant="body1" paragraph sx={{
                    marginTop: 3,
                    textAlign: 'left',
                }}>
                    {value}
                </Typography>;
            })}

            <Typography variant="body1" paragraph sx={{
                marginTop: 1,
                textAlign: 'left',
            }}>
                Best regards,
            </Typography>
            <Typography variant="body1" paragraph sx={{
                textAlign: 'left',
            }}>
                Chancellor,
            </Typography>

            <Typography variant="body1" paragraph sx={{
                textAlign: 'left',
            }}>
                Leeds Beckett University
            </Typography>
            <Typography variant="body1" paragraph sx={{
                textAlign: 'left',
            }}>
                Phone: 0113 812 0000
            </Typography>
        </Paper>
    );
}

export default Letter;