export interface EnvironmentDetails {
    LBU_AUTH_LOGIN_URL: string;
    LBU_AUTH_CREATE_URL: string;
    LBU_AUTH_GET_URL: string;
    LBU_AUTH_ACTIVATE_URL: string;
    LBU_ALL_COURSE_URL: string;
    LBU_ID_COURSE_LIST_URL: string;
    LBU_STUDENT_DETAIL_URL: string;
    LBU_STUDENT_ENROLMENT_URL: string;
    LBU_FINANCE_ACCOUNT_URL: string;
    LBU_FINANCE_INVOICE_CANCEL_URL: string;
    LBU_FINANCE_INVOICE_PAY_URL: string;
    LBU_LIBRARY_GET_ALL_BOOKS_URL: string;
    LBU_LIBRARY_BORROW_BOOKS_URL: string;
    LBU_LIBRARY_RETURN_BOOKS_URL: string;
}

export default function ServiceConstants(invoiceReference?: string) {
    const AUTH_BASE_URL = "http://localhost:8080";
    const COURSE_BASE_URL = "http://localhost:8081";
    const STUDENT_BASE_URL = "http://localhost:8082";
    const FINANCE_BASE_URL = "http://localhost:8083";
    const LIBRARY_BASE_URL = "http://localhost:8084";

    const env: EnvironmentDetails = {
        LBU_AUTH_LOGIN_URL: AUTH_BASE_URL + "/auth/login",
        LBU_AUTH_CREATE_URL: AUTH_BASE_URL + "/auth/user",
        LBU_AUTH_GET_URL: AUTH_BASE_URL + "/auth/user/",
        LBU_AUTH_ACTIVATE_URL: AUTH_BASE_URL + "/auth/activation/",
        LBU_ALL_COURSE_URL: COURSE_BASE_URL + "/courses",
        LBU_ID_COURSE_LIST_URL: COURSE_BASE_URL + "/courses/list",
        LBU_STUDENT_DETAIL_URL: STUDENT_BASE_URL + "/student",
        LBU_STUDENT_ENROLMENT_URL: STUDENT_BASE_URL + "/student/enrolment",
        LBU_FINANCE_ACCOUNT_URL: FINANCE_BASE_URL + "/finance/account",
        LBU_FINANCE_INVOICE_CANCEL_URL: FINANCE_BASE_URL + `/finance/invoice/${invoiceReference}/cancel`,
        LBU_FINANCE_INVOICE_PAY_URL: FINANCE_BASE_URL + `/finance/invoice/${invoiceReference}/pay`,
        LBU_LIBRARY_GET_ALL_BOOKS_URL: LIBRARY_BASE_URL + `/library/books`,
        LBU_LIBRARY_BORROW_BOOKS_URL: LIBRARY_BASE_URL + `/library/student/borrow/`,
        LBU_LIBRARY_RETURN_BOOKS_URL: LIBRARY_BASE_URL + `/library/student/return/`
    }
    return env;
}