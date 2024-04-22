export interface BookDto {
    id: string;
    isbn: string;
    title: string;
    author: string;
    yearOfPublished: string;
    copies: number;
    action?: "ACTION";
    isBorrowed: boolean;
}

export interface BookDtos {
    books: BookDto[]
}

export interface TransactionDto {
    id: string;
    authUserHref: string;
    bookIsbn: string;
    dateBorrowed: string;
    dateReturned: string;
    title: string;
    author: string;
    action?: "ACTION";
}

export interface TransactionDtos {
    transactions: TransactionDto[]
}