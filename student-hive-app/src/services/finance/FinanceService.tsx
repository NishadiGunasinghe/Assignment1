import ServiceConstants from "../common/ServiceConstants";
import axios, {AxiosError} from "axios";
import AuthService from "../auth/AuthService";
import {InvoiceDetailDtos} from "./FinanceDto";
import {MessageDto} from "../auth/MessageDto";

const FinanceService = {

    getFinanceAccount: async () => {
        try {
            if (ServiceConstants().LBU_FINANCE_ACCOUNT_URL) {
                const response = await axios.get(ServiceConstants().LBU_FINANCE_ACCOUNT_URL, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const courses: InvoiceDetailDtos = response.data;
                return courses;
            } else {
                throw new Error("LBU Finance service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU finance service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU finance service error. Please try again later!!");
            }
        }
    },

    cancelInvoice: async (reference: string) => {
        try {
            if (ServiceConstants(reference).LBU_FINANCE_INVOICE_CANCEL_URL) {
                const response = await axios.delete(ServiceConstants(reference).LBU_FINANCE_INVOICE_CANCEL_URL, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const courses: MessageDto = response.data;
                return courses;
            } else {
                throw new Error("LBU Finance service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU finance service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU finance service error. Please try again later!!");
            }
        }
    },

    payInvoice: async (reference: string) => {
        try {
            if (ServiceConstants(reference).LBU_FINANCE_INVOICE_PAY_URL) {
                const response = await axios.put(ServiceConstants(reference).LBU_FINANCE_INVOICE_PAY_URL, {}, {
                    headers: {
                        'Authorization': 'Bearer ' + AuthService.getJwtToken()
                    }
                });
                const courses: MessageDto = response.data;
                return courses;
            } else {
                throw new Error("LBU Finance service error. Please try again later!!");
            }
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.code === "ERR_NETWORK") {
                    throw new Error("LBU finance service error. Please try again later!!");
                } else if (error.code === "ERR_BAD_REQUEST") {
                    AuthService.signOut();
                    throw new Error("Invalid token provided. Please check your credentials and try again.");
                }
                throw new Error(error.message);
            } else {
                throw new Error("LBU finance service error. Please try again later!!");
            }
        }
    },

}

export default FinanceService;