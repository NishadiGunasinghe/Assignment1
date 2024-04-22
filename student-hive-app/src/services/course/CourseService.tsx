import ServiceConstants from "../common/ServiceConstants";
import axios, {AxiosError} from "axios";
import AuthService from "../auth/AuthService";
import {ICourses} from "./CourseDto";

const CourseService = {

    getAllCourses: async () => {
        try {
            if (ServiceConstants().LBU_ALL_COURSE_URL) {
                const response = await axios.get(ServiceConstants().LBU_ALL_COURSE_URL, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const courses: ICourses = response.data;
                return courses;
            } else {
                throw new Error("LBU Course service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU Course service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU Course service error. Please try again later!!");
            }
        }
    },

    getCoursesForList: async (courseIds: string[]) => {
        try {
            if (ServiceConstants().LBU_ID_COURSE_LIST_URL) {
                const response = await axios.post(ServiceConstants().LBU_ID_COURSE_LIST_URL, courseIds, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const courses: ICourses = response.data;
                return courses;
            } else {
                throw new Error("LBU Course service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU Course service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU Course service error. Please try again later!!");
            }
        }
    },

}

export default CourseService;