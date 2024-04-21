import {createBrowserRouter, Navigate} from "react-router-dom";
import Home from "./pages/home/Home";
import ViewAllCourse from "./pages/course/ViewAllCourse";
import Finance from "./pages/finance/Finance";
import Graduation from "./pages/graduation/Graduation";
import Profile from "./pages/profile/Profile";
import SignIn from "./pages/login/SignIn";
import AuthService from "./services/auth/AuthService";
import SignUp from "./pages/signup/SignUp";
import Activation from "./pages/activation/Activation";
import EnrolledCourses from "./pages/course/EnrolledCourses";
import {jwtDecode} from "jwt-decode";
import {JwtDto} from "./services/auth/AuthDto";
import ViewAllBooks from "./pages/library/Library";
import ManageCourses from "./pages/admin/course/ManageCourses";
import ErrorView from "./pages/error/ErrorView";
import NewBooks from "./pages/admin/library/NewBooks";
import LoanAndOverdueBooks from "./pages/admin/library/LoanAndOverdueBooks";
import StudentBorrowAndOverdue from "./pages/admin/library/StudentBorrowAndOverdue";
import Library from "./pages/library/Library";

// Define a function to check if the user is authenticated
const isAuthenticated = () => {
    return AuthService.isAuthenticated();
};


const token = AuthService.getJwtToken();

let initPayLoad: JwtDto = {
    roles: "",
    firstName: "",
    iat: 0,
    exp: 0,
    jti: "",
    lastName: "",
    sub: "",
    iss: "",
    userId: ""
};

if (token) {
    try {
        initPayLoad = jwtDecode(token);
    } catch (error) {
        console.error('Error decoding JWT token:', error);
    }
}

// Define a private route component
// @ts-ignore
const PrivateRoute = ({element, ...rest}) => {
    return isAuthenticated() ? (element) :
        (<Navigate to="/signin" replace state={{from: rest.location}}/>);
};

export const router = createBrowserRouter([
    {
        path: "/",
        element: <PrivateRoute element={<Home jwtPayload={initPayLoad}/>}/>
    },
    {
        path: "/signin",
        element: <SignIn/>
    },
    {
        path: "/signup",
        element: <SignUp/>
    },
    {
        path: "/activation/:token",
        element: <Activation/>
    },
    {
        path: '/courses',
        element: <PrivateRoute element={<ViewAllCourse jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/admin/courses/management',
        element: <PrivateRoute element={<ManageCourses jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/course/enroll',
        element: <PrivateRoute element={<EnrolledCourses jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/finance',
        element: <PrivateRoute element={<Finance jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/graduation',
        element: <PrivateRoute element={<Graduation jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/library/books',
        element: <PrivateRoute element={<Library jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/admin/library/books',
        element: <PrivateRoute element={<NewBooks jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/admin/library/lends',
        element: <PrivateRoute element={<LoanAndOverdueBooks jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/admin/library/student/lends',
        element: <PrivateRoute element={<StudentBorrowAndOverdue jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/profile',
        element: <PrivateRoute element={<Profile jwtPayload={initPayLoad}/>}/>
    },
    {
        path: '/error',
        element: <ErrorView/>
    }
])