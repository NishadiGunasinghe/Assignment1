import ServiceConstants from "../common/ServiceConstants";
import axios, {AxiosError} from "axios";
import AuthService from "../auth/AuthService";
import {IStudentDto} from "./IStudentDto";

const StudentService = {

    getStudentDetail: async () => {
        try {
            if (ServiceConstants().LBU_STUDENT_DETAIL_URL) {
                const response = await axios.get(ServiceConstants().LBU_STUDENT_DETAIL_URL
                    + "?authUserHref=/auth/user/" + AuthService.getAuthUserId(), {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const courses: IStudentDto = response.data;
                return courses;
            } else {
                throw new Error("LBU Student service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU Student service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU Student service error. Please try again later!!");
            }
        }
    },


    enrollCourses: async (courseHref: string) => {
        try {
            if (ServiceConstants().LBU_STUDENT_ENROLMENT_URL) {
                const response = await axios.post(ServiceConstants().LBU_STUDENT_ENROLMENT_URL, {
                    authUserHref: "/auth/user/" + AuthService.getAuthUserId(),
                    courseHref: courseHref
                }, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const courses: IStudentDto = response.data;
                return courses;
            } else {
                throw new Error("LBU Student service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU Student service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU Student service error. Please try again later!!");
            }
        }
    },
}

export default StudentService;