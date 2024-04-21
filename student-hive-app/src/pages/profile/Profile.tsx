import React, {useEffect, useState} from 'react';
import {
    TextField,
    Grid,
    Typography,
    IconButton,
    InputAdornment,
    Box,
    Icon,
    Avatar,
    Divider, CircularProgress, Backdrop
} from '@mui/material';
import {Edit as EditIcon} from '@mui/icons-material';
import Layout from "../../layout/Layout";
import Button from "@mui/material/Button";
import ErrorDialog from "../../components/error/ErrorDialog";
import AuthService from "../../services/auth/AuthService";
import {AuthUserDto} from "../../services/auth/AuthDto";

// @ts-ignore
const Profile = ({ jwtPayload:JwtDto }) => {
    const [profile, setProfile] = useState({
        firstName: '',
        lastName: '',
        email: '',
    });

    const[updatable,setUpdatable] = useState<boolean>(false);

    const [editableFields, setEditableFields] = useState({
        firstName: false,
        lastName: false,
        email: false,
        // Add other editable fields here
    });

    const handleEdit = (field: any) => {
        setEditableFields({...editableFields, [field]: true});
    };

    const handleSave = (field: any) => {
        setEditableFields({...editableFields, [field]: false});
    };

    const handleChange = (e: any) => {
        setProfile({...profile, [e.target.name]: e.target.value});
        setUpdatable(true);
    };

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
                const authUserDto: AuthUserDto = await AuthService.getAuthUser();
                setProfile({
                    email: authUserDto.email,
                    firstName: authUserDto.firstName,
                    lastName: authUserDto.lastName
                })
                handleClose()
            } catch (error) {
                // @ts-ignore
                if (error.message) {
                    // @ts-ignore
                    setError(error.message);
                }
                handleClose()
            }
        };

        fetchData();

        return () => {
        }
    }, []);

    return (
        <Layout>
            <Box sx={{bgcolor: '#ffffff', padding: 2}}>
                <form>
                    <Grid container spacing={2} direction="column">
                        <Grid item>
                            <Avatar sx={{
                                width: '120px', // Adjust the width as needed
                                height: '120px', // Adjust the height as needed
                                fontSize: '60px',
                                backgroundColor: 'lightgray', // Adjust the background color as needed
                                marginRight: '16px', // Adjust the right margin as needed
                            }}>{profile.firstName.slice(0, 1).toUpperCase().concat(profile.lastName.slice(0, 1).toUpperCase())}</Avatar>
                        </Grid>
                        <Grid item xs={12} sm container direction="column">
                            <Grid item>
                                <Typography variant="h6">Profile Information</Typography>
                            </Grid>
                            <Divider sx={{
                                marginTop: '25px', // Adjust the top margin as needed
                                marginBottom: '25px', // Adjust the bottom margin as needed
                            }}/>


                            <Box component="form" noValidate sx={{mt: 3}}>
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
                                            value={profile.firstName}
                                            disabled={!editableFields.firstName}
                                            onChange={handleChange}
                                            InputProps={{
                                                endAdornment: (
                                                    <InputAdornment position="end">
                                                        {!editableFields.firstName ? (
                                                            <IconButton onClick={() => handleEdit('firstName')}>
                                                                <EditIcon/>
                                                            </IconButton>
                                                        ) : (
                                                            <IconButton onClick={() => handleSave('firstName')}>
                                                                <Icon>save</Icon>
                                                            </IconButton>
                                                        )}
                                                    </InputAdornment>
                                                ),
                                            }}
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
                                            value={profile.lastName}
                                            disabled={!editableFields.lastName}
                                            onChange={handleChange}
                                            InputProps={{
                                                endAdornment: (
                                                    <InputAdornment position="end">
                                                        {!editableFields.lastName ? (
                                                            <IconButton onClick={() => handleEdit('lastName')}>
                                                                <EditIcon/>
                                                            </IconButton>
                                                        ) : (
                                                            <IconButton onClick={() => handleSave('lastName')}>
                                                                <Icon>save</Icon>
                                                            </IconButton>
                                                        )}
                                                    </InputAdornment>
                                                ),
                                            }}
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
                                            value={profile.email}
                                            disabled={!editableFields.email}
                                            onChange={handleChange}
                                            InputProps={{
                                                endAdornment: (
                                                    <InputAdornment position="end">
                                                        {!editableFields.email ? (
                                                            <IconButton onClick={() => handleEdit('email')}>
                                                                <EditIcon/>
                                                            </IconButton>
                                                        ) : (
                                                            <IconButton onClick={() => handleSave('email')}>
                                                                <Icon>save</Icon>
                                                            </IconButton>
                                                        )}
                                                    </InputAdornment>
                                                ),
                                            }}
                                        />
                                    </Grid>
                                    <Grid item xs={1}>
                                        <Button
                                            type="submit"
                                            fullWidth
                                            variant="contained"
                                            disabled={!updatable}
                                            sx={{mt: 3, mb: 2}}
                                        >
                                            Update
                                        </Button>
                                    </Grid>
                                </Grid>
                            </Box>
                        </Grid>
                    </Grid>
                </form>
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
    );
};

export default Profile;
