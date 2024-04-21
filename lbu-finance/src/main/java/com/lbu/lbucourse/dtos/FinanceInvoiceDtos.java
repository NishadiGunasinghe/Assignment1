package com.lbu.lbucourse.dtos;

import lombok.Data;

import java.util.List;

@Data
public class FinanceInvoiceDtos {
    List<FinanceInvoiceDto> invoices;
}
