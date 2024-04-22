import React, {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {Button, Grid, Icon, Typography} from "@mui/material";
import AuthService from "../../services/auth/AuthService";
import {MessageDto} from "../../services/auth/MessageDto";

export default function Activation() {
    const {token} = useParams(); // Get the token from the URL
    const [status, setStatus] = useState<boolean>(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                if (token) {
                    let messageDto: MessageDto | undefined = await AuthService.activateAccount(token);
                    if (messageDto) {
                        setStatus(true);
                    } else {
                        setStatus(false);
                    }
                } else {
                    setStatus(false);
                }
            } catch (e) {
                setStatus(false);
            }
        };
        fetchData();
        return () => {}
    }, [token]);

    return (
        <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh'}}>
            <Grid container direction="column" alignItems="center" spacing={3}>
                <Grid item>
                    <Icon
                        style={{
                            color: status ? '#207221' : '#dc4538',
                            fontSize: 80
                        }}>{status ? 'check_circle_icon' : 'error'}</Icon>
                </Grid>
                <Grid item>
                    <Typography variant="h4"
                                style={{color: status ? '#207221' : '#dc4538'}}>{status ? 'Activation Success!' : 'Activation Failed!'}</Typography>
                </Grid>
                <Grid item>
                    <Typography variant="body1">
                        {status ? 'Your account has been successfully activated. Go back to the login page'
                            : 'Your account activation is failed, Please contact system administrator.'}
                    </Typography>
                </Grid>
                <Grid item>
                    <Button
                        variant="contained"
                        color="primary"
                        component={Link}
                        to="/signin"
                        style={{marginTop: 30}}>
                        Go to Sign In
                    </Button>
                </Grid>
            </Grid>
        </div>
    );
}