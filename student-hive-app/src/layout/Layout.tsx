import React, {ReactNode, useEffect, useState} from "react";
import {Box, CssBaseline} from "@mui/material";
import SideBar from "../components/sidebar/Sidebar";
import TopAppBar from "../components/appbar/AppBar";
import Toolbar from "@mui/material/Toolbar";
import AuthService from "../services/auth/AuthService";
import {jwtDecode} from "jwt-decode";
import {JwtDto} from "../services/auth/AuthDto";

interface LayoutProps {
    children: ReactNode
}

const Layout = ({children}: LayoutProps) => {

    const [jwtPayload, setJwtPayLoad] = useState<JwtDto>({
        roles: '',
        firstName: '',
        lastName: '',
        iat: 0,
        iss: '',
        jti: '',
        exp: 0,
        sub: '',
        userId: ''
    });

    useEffect(() => {
        const token = AuthService.getJwtToken();
        if (token) {
            try {
                const decoded: JwtDto = jwtDecode(token);
                setJwtPayLoad(decoded);
            } catch (error) {
                console.error('Error decoding JWT token:', error);
            }
        }
        return () => {
        }
    }, []);


    // @ts-ignore
    return (
        <Box sx={{display: 'flex'}}>
            <CssBaseline/>
            <TopAppBar tokenData={jwtPayload}/>
            <SideBar tokenData={jwtPayload}/>
            <Box component="main" sx={{flexGrow: 1}}>
                <Toolbar/>
                {children}
            </Box>
        </Box>
    );
}

export default Layout;