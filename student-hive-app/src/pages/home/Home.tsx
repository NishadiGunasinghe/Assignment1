import React from "react";
import Layout from "../../layout/Layout";
import {Icon, Typography} from "@mui/material";

// @ts-ignore
const Home = ({ jwtPayload:JwtDto }) => {
    return <Layout>
        <div style={{
            minHeight: '100vh',
            width: "100%",
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            backgroundColor: "#10141F",
            padding: 0
        }}>
            <Icon sx={{color: "white", fontSize: "100px"}}>school</Icon>
            <Typography id={"homeWelcome"} variant="h2" align="center" gutterBottom fontWeight={700}>
                <span style={{color: "white"}}>Welcome to</span>{" "}
                <span style={{color: "orange"}}> Student</span>{" "}
                <span style={{color: "white"}}>Hive</span>
            </Typography>
            <Typography
                variant={"h2"}
                fontWeight={700}
                fontSize={18}
            >
            </Typography>
            <Typography variant="body1" align="center" paragraph>
                <span
                    style={{color: "white"}}>Empowering Excellence: Navigating Academia with Precision and Care</span>{" "}
            </Typography>
        </div>
    </Layout>
}

export default Home;