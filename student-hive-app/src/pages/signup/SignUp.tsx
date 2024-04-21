import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {AuthUserDto} from "../../services/auth/AuthDto";
import {useState} from "react";
import ErrorDialog from "../../components/error/ErrorDialog";
import AuthService from "../../services/auth/AuthService";
import SuccessDialog from "../../components/error/SuccessDialog";
import {Backdrop, CircularProgress} from "@mui/material";

function Copyright(props: any) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            {'Copyright © '}
            <Link color="inherit" href="https://mui.com/">Student Hive</Link>{' '}
            {new Date().getFullYear()}
            {'.'}
        </Typography>
    );
}

export default function SignUp() {

    const [error, setError] = useState<string | null>(null);
    const handleCloseErrorDialog = () => {
        setError(null);
    };

    const [success, setSuccess] = useState<string | null>(null);
    const handleCloseSuccessDialog = () => {
        setSuccess(null);
        window.location.href = '/signin';
    };

    const [open, setOpen] = React.useState(false);
    const handleClose = () => {
        setOpen(false);
    };
    const handleToggle = () => {
        setOpen(!open);
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        handleToggle()
        event.preventDefault();
        const data = new FormData(event.currentTarget);
        const email: string = data.get('email')?.toString() || '';
        const password = data.get('password')?.toString() || '';
        const userName = data.get('userName')?.toString() || '';
        const firstName = data.get('firstName')?.toString() || '';
        const lastName = data.get('firstName')?.toString() || '';

        if (email !== '' && password !== '' && userName !== '' && firstName !== '' && lastName !== '') {
            try {
                const createAccount: AuthUserDto = {
                    email: email,
                    password: password,
                    userName: userName,
                    firstName: firstName,
                    lastName: firstName
                }
                await AuthService.createAuthUser(createAccount);
                handleClose()
                setSuccess("Successfully created the account for email: " + email + " and user: " + userName);
            } catch (error) {
                // @ts-ignore
                if (error.message) {
                    // @ts-ignore
                    setError(error.message);
                }
                handleClose()
            }
        } else {
            handleClose()
            setError("It appears that certain required fields have not been completed. To proceed, kindly ensure all mandatory fields are filled out appropriately.")
        }
    };

    return (
        <>
            <Container component="main" maxWidth="xs">
                <CssBaseline/>
                <Box
                    sx={{
                        marginTop: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
                        <LockOutlinedIcon/>
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        Sign up
                    </Typography>
                    <Box component="form" noValidate onSubmit={handleSubmit} sx={{mt: 3}}>
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    autoComplete="given-name"
                                    name="firstName"
                                    required
                                    fullWidth
                                    id="firstName"
                                    label="First Name"
                                    autoFocus
                                />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    required
                                    fullWidth
                                    id="lastName"
                                    label="Last Name"
                                    name="lastName"
                                    autoComplete="family-name"
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    id="email"
                                    label="Email Address"
                                    name="email"
                                    autoComplete="email"
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    name="userName"
                                    label="User Name"
                                    id="userName"
                                    autoComplete="new-password"
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    name="password"
                                    label="Password"
                                    type="password"
                                    id="password"
                                    autoComplete="new-password"
                                />
                            </Grid>
                        </Grid>
                        <Button
                            id={"btnSignIn"}
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                        >
                            Sign Up
                        </Button>
                        <Grid container justifyContent="flex-end">
                            <Grid item>
                                <Link href="/signin" variant="body2">
                                    Already have an account? Sign in
                                </Link>
                            </Grid>
                        </Grid>
                    </Box>
                </Box>
                <Copyright sx={{mt: 5}}/>
            </Container>

            <ErrorDialog open={!!error} onClose={handleCloseErrorDialog} message={error || ''} id={"signUpError"}/>
            <SuccessDialog open={!!success} onClose={handleCloseSuccessDialog} message={success || ''} id={"success"}/>
            <Backdrop
                sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
                open={open}
                onClick={handleClose}
            >
                <CircularProgress color="inherit"/>
            </Backdrop>
        </>
    );
}