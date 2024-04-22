export interface InvoiceDetailDtos {
    id: string,
    authUserHref: string,
    invoiceList: InvoiceDetailDto[],
}

export interface InvoiceDetailDto {
    id: string,
    reference: string,
    dueDate: string,
    type: string,
    status: string,
    amount: number,
    action?: "ACTION";
}