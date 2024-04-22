import React from "react";
import {Link, useLocation} from "react-router-dom";
import {
    Box, Collapse, Divider,
    Drawer,
    Icon,
    List, ListItem, ListItemButton, ListItemIcon, ListItemText
} from "@mui/material";
import Toolbar from "@mui/material/Toolbar";
import AuthService from "../../services/auth/AuthService";
import {JwtDto} from "../../services/auth/AuthDto";

const navLinks = [
    {
        icon: "home",
        name: "Home",
        link: "/",
        accessLevel: ["ROLE_GENERAL_USER", "ROLE_STUDENT", "ROLE_ADMIN"]
    },
    {
        icon: "subject",
        name: "Course",
        accessLevel: ["ROLE_GENERAL_USER", "ROLE_STUDENT", "ROLE_ADMIN"],
        subMenu: [
            {
                name: "All Courses",
                link: "/courses",
                accessLevel: ["ROLE_GENERAL_USER", "ROLE_STUDENT", "ROLE_ADMIN"]
            },
            {
                name: "Enrolled Courses",
                link: "/course/enroll",
                accessLevel: ["ROLE_STUDENT", "ROLE_ADMIN"]
            },
            {
                name: "Manage Courses",
                link: "/admin/courses/management",
                accessLevel: ["ROLE_ADMIN"]
            }
        ]
    },
    {
        icon: "account_circle",
        name: "Profile",
        link: "/profile",
        accessLevel: ["ROLE_GENERAL_USER", "ROLE_STUDENT", "ROLE_ADMIN"]
    },
    {
        icon: "school",
        name: "Graduation",
        link: "/graduation",
        accessLevel: ["ROLE_STUDENT", "ROLE_ADMIN"]
    },
    {
        icon: "account_balance",
        name: "Finance",
        link: "/finance",
        accessLevel: ["ROLE_STUDENT", "ROLE_ADMIN"]
    },
    {
        icon: "library_books",
        name: "Library",
        accessLevel: ["ROLE_STUDENT", "ROLE_ADMIN"],
        subMenu: [
            {
                name: "View Books",
                link: "/library/books",
                accessLevel: ["ROLE_STUDENT", "ROLE_ADMIN"]
            },
            {
                name: "New Books",
                link: "/admin/library/books",
                accessLevel: ["ROLE_ADMIN"]
            },
            {
                name: "All Lends",
                link: "/admin/library/lends",
                accessLevel: ["ROLE_ADMIN"]
            },
            {
                name: "Student Lends",
                link: "/admin/library/student/lends",
                accessLevel: ["ROLE_ADMIN"]
            },
        ]
    },
    {
        icon: "exit_to_app",
        name: "Logout",
        accessLevel: ["ROLE_GENERAL_USER", "ROLE_STUDENT", "ROLE_ADMIN"]
    },
];
const drawerWidth = 240;

export interface SideBarProp {
    tokenData: JwtDto
}

const SideBar = (props: SideBarProp) => {
    const [dropDownStatus, setDropDownStatus] = React.useState({
        Course: false,
        Library: false
    })

    function handleClick(dropDownType: any) {
        if (dropDownType === "Course") {
            setDropDownStatus({
                Course: !dropDownStatus.Course,
                Library: dropDownStatus.Library
            })
        } else {
            setDropDownStatus({
                Course: dropDownStatus.Course,
                Library: !dropDownStatus.Library
            })
        }
    }

    function isOpen(name: string) {
        if (name === "Course") {
            return dropDownStatus.Course;
        } else {
            return dropDownStatus.Library;
        }
    }

    function handleLogout() {
        AuthService.signOut();
    }

    return (
        <Drawer
            variant="permanent"
            sx={{
                width: drawerWidth,
                flexShrink: 0,
                [`& .MuiDrawer-paper`]: {width: drawerWidth, boxSizing: 'border-box', backgroundColor: "#161d2f"},
                backgroundColor: "#161d2f"
            }}
        >
            <Toolbar/>
            <Box sx={{overflow: 'auto', backgroundColor: "#161d2f", color: "white"}}>
                <List>
                    {navLinks.filter(value => value.accessLevel.includes(props.tokenData.roles)).map((item, index) => {
                        if (item.subMenu) {
                            return <div>
                                <ListItem key={item.name} disablePadding sx={{
                                    "&:hover": {
                                        backgroundColor: "orange",
                                    }
                                }}>
                                    <ListItemButton id={"btn" + item.name} onClick={() => handleClick(item.name)}>
                                        <ListItemIcon>
                                            <Icon sx={{
                                                color: "white",
                                                alignItems: "left",
                                                justifyContent: "left",
                                                marginRight: 3
                                            }}>{item.icon}</Icon>
                                        </ListItemIcon>
                                        <ListItemText id={"lItemText" + item.name} primary={item.name}/>
                                        {isOpen(item.name) ? <Icon sx={{
                                            color: "white",
                                            alignItems: "left",
                                            justifyContent: "left",
                                            marginRight: 3
                                        }}>expand_less</Icon> : <Icon sx={{
                                            color: "white",
                                            alignItems: "left",
                                            justifyContent: "left",
                                            marginRight: 3
                                        }}>expand_more</Icon>}
                                    </ListItemButton>
                                </ListItem>
                                <Collapse in={isOpen(item.name)} timeout="auto" unmountOnExit>
                                    <Divider/>
                                    <List component="div" disablePadding>
                                        {item.subMenu.filter(value => value.accessLevel.includes(props.tokenData.roles)).map((subItem) => {
                                            return <ListItemButton id={"btn" + subItem.name} component={Link}
                                                                   to={subItem.link} sx={{
                                                "&:hover": {
                                                    backgroundColor: "orange",
                                                }
                                            }}>
                                                <ListItemText id={"lItemText" + subItem.name} inset
                                                              primary={subItem.name}/>
                                            </ListItemButton>
                                        })}
                                    </List>
                                </Collapse>
                            </div>;
                        } else {
                            if (item.link) {
                                return <ListItem key={item.name} disablePadding sx={{
                                    "&:hover": {
                                        backgroundColor: "orange",
                                    }
                                }}>
                                    <ListItemButton id={"btn" + item.name} component={Link} to={item.link}>
                                        <ListItemIcon>
                                            <Icon sx={{
                                                color: "white",
                                                alignItems: "left",
                                                justifyContent: "left",
                                                marginRight: 3
                                            }}>{item.icon}</Icon>
                                        </ListItemIcon>
                                        <ListItemText id={"lItemText" + item.name} primary={item.name}/>
                                    </ListItemButton>
                                </ListItem>;
                            } else {
                                return <ListItem key={item.name} disablePadding sx={{
                                    "&:hover": {
                                        backgroundColor: "orange",
                                    }
                                }}>
                                    <ListItemButton id={"btn" + item.name} onClick={handleLogout}>
                                        <ListItemIcon>
                                            <Icon sx={{
                                                color: "white",
                                                alignItems: "left",
                                                justifyContent: "left",
                                                marginRight: 3
                                            }}>{item.icon}</Icon>
                                        </ListItemIcon>
                                        <ListItemText id={"lItemText" + item.name} primary={item.name}/>
                                    </ListItemButton>
                                </ListItem>;
                            }
                        }
                    })}
                </List>
            </Box>
        </Drawer>
    );
};

export default SideBar;
