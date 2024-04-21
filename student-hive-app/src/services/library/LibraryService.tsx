import ServiceConstants from "../common/ServiceConstants";
import axios, {AxiosError} from "axios";
import AuthService from "../auth/AuthService";
import {BookDtos} from "./LibraryDto";
import {MessageDto} from "../auth/MessageDto";

const LibraryService = {

    getAllBooks: async () => {
        try {
            if (ServiceConstants().LBU_LIBRARY_GET_ALL_BOOKS_URL) {
                const response = await axios.get(ServiceConstants().LBU_LIBRARY_GET_ALL_BOOKS_URL, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const books: BookDtos = response.data;
                return books;
            } else {
                throw new Error("LBU Library service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU library service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU library service error. Please try again later!!");
            }
        }
    },

    borrowBook: async (isbn: string) => {
        try {
            if (ServiceConstants().LBU_LIBRARY_BORROW_BOOKS_URL) {
                const response = await axios.post(ServiceConstants().LBU_LIBRARY_BORROW_BOOKS_URL + isbn, {}, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const message: MessageDto = response.data;
                return message;
            } else {
                throw new Error("LBU Library service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU library service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU library service error. Please try again later!!");
            }
        }
    },

    returnBook: async (isbn: string) => {
        try {
            if (ServiceConstants().LBU_LIBRARY_RETURN_BOOKS_URL) {
                const response = await axios.post(ServiceConstants().LBU_LIBRARY_RETURN_BOOKS_URL + isbn, {}, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const message: MessageDto = response.data;
                return message;
            } else {
                throw new Error("LBU Library service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU library service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU library service error. Please try again later!!");
            }
        }
    }
}

export default LibraryService;