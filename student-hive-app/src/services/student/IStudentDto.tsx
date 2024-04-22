import {JWTTokenDto} from "../auth/AuthDto";

export interface IStudentDto {
    id: string,
    address?: string,
    emergencyContact?: string,
    dateOfBirth?: string,
    phoneContact?: string,
    authUserHref: string,
    createdTimestamp: string,
    updatedTimestamp: string,
    courseHrefs: string[],
    jwtTokenDto?: JWTTokenDto
}