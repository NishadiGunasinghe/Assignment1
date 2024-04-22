export interface AuthDto {
    userName: string;
    password: string;
}

export interface JWTTokenDto {
    jwtToken: string;
    userId: string;
}

export interface AuthUserDto {
    userId?: string;
    userName: string;
    password: string;
    firstName: string,
    lastName: string,
    email: string
}

export interface JwtDto {
    exp: number,
    firstName: string,
    iat: number,
    iss: string,
    jti: string,
    lastName: string,
    roles: string,
    sub: string,
    userId: string
}