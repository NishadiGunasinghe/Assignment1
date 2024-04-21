import ServiceConstants from "../common/ServiceConstants";
import {AuthDto, AuthUserDto, JwtDto, JWTTokenDto} from "./AuthDto";
import {MessageDto} from "./MessageDto";
import axios, {AxiosError} from 'axios';

const AuthService = {

    signIn: async (username: string, password: string) => {
        try {
            let loginData: AuthDto = {
                password: password,
                userName: username
            }
            const response = await axios.post(ServiceConstants().LBU_AUTH_LOGIN_URL, loginData);
            const authData: JWTTokenDto = response.data;
            localStorage.setItem('token', authData.jwtToken);
            localStorage.setItem('userId', authData.userId);
            return authData.jwtToken;
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU Auth service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    let messageDto: MessageDto = error.response?.data
                    if (messageDto) {
                        if (messageDto.code === 4000 || messageDto.code === 4003) {
                            throw new Error(messageDto.message);
                        }
                    } else {
                        throw new Error("Invalid username or password. Please check your credentials and try again.");
                    }
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU Auth service error. Please try again later!!");
            }
        }
    },

    signOut: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        window.location.href = '/';
    },

    getJwtToken: () => {
        return localStorage.getItem('token')
    },

    getAuthUserId: () => {
        return localStorage.getItem('userId')
    },

    isAuthenticated: () => {
        const token = localStorage.getItem('token');
        return !!token;
    },

    async activateAccount(token: string) {
        try {
            const response = await axios.get(ServiceConstants().LBU_AUTH_ACTIVATE_URL + token);
            const message: MessageDto = response.data;
            if (message) {
                return message;
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU Auth service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    throw new Error("Invalid username or password. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU Auth service error. Please try again later!!");
            }
        }
    },

    async createAuthUser(createAccount: AuthUserDto) {
        try {
            const response = await axios.post(ServiceConstants().LBU_AUTH_CREATE_URL, createAccount);
            const authData: AuthUserDto = response.data;
            if (authData.userId) {
                localStorage.setItem('userId', authData.userId);
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU Auth service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    let messageDto: MessageDto = error.response?.data
                    if (messageDto) {
                        if (messageDto.code === 4006) {
                            throw new Error(messageDto.message);
                        }
                    } else {
                        throw new Error("Invalid username or password. Please check your credentials and try again.");
                    }
                    throw new Error("Invalid username or password. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU Auth service error. Please try again later!!");
            }
        }
    },

    async getAuthUser() {
        try {
            const response = await axios.get(ServiceConstants().LBU_AUTH_GET_URL + this.getAuthUserId(), {
                headers: {
                    'Authorization': 'Bearer ' + this.getJwtToken()
                }
            });
            const authData: AuthUserDto = response.data;
            return authData;
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU Auth service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    let messageDto: MessageDto = error.response?.data
                    if (messageDto) {
                        throw new Error(messageDto.message);
                    } else {
                        throw new Error("Invalid username or password. Please check your credentials and try again.");
                    }
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU Auth service error. Please try again later!!");
            }
        }
    },

    checkIsAdminOrStudent: (jwtPayload: JwtDto) => {
        return ["ROLE_STUDENT", "ROLE_ADMIN"].includes(jwtPayload.roles)
    }
};

export default AuthService;
