
export interface ICourses {
    courses: Array<ICourse>;
}

export interface ICourse {
    "idHref": string,
    "title": string,
    "description": string,
    "fees": number,
    "durationInDays": number,
    "instructor": string
}