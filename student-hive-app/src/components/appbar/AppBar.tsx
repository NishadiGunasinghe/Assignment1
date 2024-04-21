import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import {JwtDto} from "../../services/auth/AuthDto";
import {Avatar} from "@mui/material";

export interface MenuAppBarProp {
    tokenData: JwtDto
}

export default function MenuAppBar(props: MenuAppBarProp) {
    const [auth, setAuth] = React.useState(true);
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

    const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    return (
        <AppBar position="fixed" sx={{zIndex: (theme) => theme.zIndex.drawer + 1, backgroundColor: "#161d2f"}}>
            <Toolbar>
                <Typography
                    variant={"h5"}
                    component={"div"}
                    my={2}
                    fontWeight={700}
                    fontSize={18}
                    sx={{flexGrow: 1}}
                >
                    <span style={{color: "orange"}}>Student</span>{" "}
                    <span style={{color: "white"}}>Hive</span>
                </Typography>
                {auth && (
                    <div>
                        <Avatar sx={{
                            width: '40px', // Adjust the width as needed
                            height: '40px', // Adjust the height as needed
                            fontSize: '18px',
                            backgroundColor: '#fc914f', // Adjust the background color as needed
                            marginRight: '2px', // Adjust the right margin as needed
                        }}>{props.tokenData.firstName.slice(0, 1).toUpperCase().concat(props.tokenData.lastName.slice(0, 1).toUpperCase())}</Avatar>
                    </div>
                )}
            </Toolbar>
        </AppBar>
    );
}