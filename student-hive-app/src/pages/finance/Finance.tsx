import React, {useEffect, useState} from "react";
import Layout from "../../layout/Layout";
import {
    Backdrop,
    Box,
    CircularProgress,
    Divider,
    Grid, Icon, IconButton, Table, TableBody, TableCell,
    TableContainer, TableHead, TablePagination, TableRow,
    Typography
} from "@mui/material";
import ErrorDialog from "../../components/error/ErrorDialog";
import {InvoiceDetailDtos} from "../../services/finance/FinanceDto";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import FinanceService from "../../services/finance/FinanceService";
import ConfirmationDialog from "../../components/error/ConfirmationDialog";
import SuccessDialog from "../../components/error/SuccessDialog";

interface Column {
    id: 'reference' | 'dueDate' | 'type' | 'status' | 'amount' | 'action';
    label: string;
    minWidth?: number;
    align?: 'right';
    format?: (value: any) => any;

}

const counter: number = 1;
const columns: readonly Column[] = [
    {
        id: 'reference',
        label: 'Reference',
        minWidth: 100,
        align: 'right',
        format: (value: any) => value.toLocaleString('en-US'),
    },
    {
        id: 'dueDate',
        label: 'Due Date',
        minWidth: 100,
        align: 'right',
        format: (value: any) => value.toLocaleString('en-US'),
    },
    {
        id: 'type',
        label: 'Invoice Type',
        minWidth: 100,
        align: 'right',
        format: (value: any) => {
            if (value === 'TUITION_FEES') {
                return 'Tuition Fee';
            } else if (value === 'LIBRARY_FINE') {
                return 'Library Fine';
            } else {
                return '';
            }
        },
    },
    {
        id: 'status',
        label: 'Invoice Status',
        minWidth: 100,
        align: 'right',
        format: (value: any) => {
            if (value === 'OUTSTANDING') {
                return 'Outstanding';
            } else if (value === 'PAID') {
                return 'Paid';
            } else if (value === 'CANCELLED') {
                return 'Cancelled';
            } else {
                return '';
            }
        },
    },
    {
        id: 'amount',
        label: 'Amount',
        minWidth: 100,
        align: 'right',
        format: (value: any) => {
            return value + " £"
        },
    },
    {
        id: 'action',
        label: 'Action',
        minWidth: 100,
        align: 'right'
    }
];

// @ts-ignore
const Finance = ({jwtPayload: JwtDto}) => {

    let [invoiceList, setInvoices] = useState<InvoiceDetailDtos>()
    const [error, setError] = useState<string | null>(null);
    const handleCloseErrorDialog = () => {
        setError(null);
    };

    const [success, setSuccessMessage] = React.useState<string | null>(null);
    const handleSuccessDialog = () => {
        setSuccessMessage(null);
        window.location.reload();
    }

    const [open, setOpen] = React.useState(false);
    const handleClose = () => {
        setOpen(false);
    };
    const handleToggle = () => {
        setOpen(!open);
    };

    useEffect(() => {
        handleToggle()
        const fetchData = async () => {
            try {
                const financeAccountData = await FinanceService.getFinanceAccount();
                setInvoices(financeAccountData);
                handleClose();
            } catch (error) {
                // @ts-ignore
                if (error.message) {
                    // @ts-ignore
                    setError(error.message);
                }
                handleClose();
            }
        };
        fetchData();
        return () => {
        }
    }, []);

    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };


    const [invoiceRef, setInvoiceRef] = useState<string | null>(null);
    const [paymentConfirmMessage, setPaymentConfirmMessage] = useState<string | null>(null);
    const [paymentCancelConfirmMessage, setPaymentCancelConfirmMessage] = useState<string | null>(null);
    const paymentHandling = (reference: string) => {
        setInvoiceRef(reference);
        setPaymentConfirmMessage("Do you wanted to pay ?");
    }

    const paymentCancelHandling = (reference: string) => {
        setInvoiceRef(reference);
        setPaymentCancelConfirmMessage("Do you wanted to cancel the pay ?");
    }

    const payApprove = () => {
        handleToggle()
        const fetchData = async () => {
            if (invoiceRef) {
                try {
                    const messageDto = await FinanceService.payInvoice(invoiceRef);
                    setSuccessMessage(messageDto.message);
                    handleClose();
                } catch (error) {
                    // @ts-ignore
                    if (error.message) {
                        // @ts-ignore
                        setError(error.message);
                    }
                    handleClose();
                }
            } else {
                handleClose();
            }
        };
        fetchData();
    }

    const payDeclined = () => {
        handleToggle()
        const fetchData = async () => {
            if (invoiceRef) {
                try {
                    const messageDto = await FinanceService.cancelInvoice(invoiceRef);
                    setSuccessMessage(messageDto.message);
                    handleClose();
                } catch (error) {
                    // @ts-ignore
                    if (error.message) {
                        // @ts-ignore
                        setError(error.message);
                    }
                    handleClose();
                }
            } else {
                handleClose();
            }
        };
        fetchData();
    }

    const closeDialog = () => {
        setPaymentConfirmMessage(null);
        setPaymentCancelConfirmMessage(null);
    }

    return <Layout>
        <Box sx={{bgcolor: '#ffffff', padding: 2}}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Grid item>
                        <Typography variant="h4">Finance Summery</Typography>
                    </Grid>
                    <Divider sx={{
                        marginTop: '25px', // Adjust the top margin as needed
                        marginBottom: '25px', // Adjust the bottom margin as needed
                    }}/>
                </Grid>

                <Grid item xs={12}>
                    <Card>
                        <CardContent>

                            <TableContainer sx={{maxHeight: 440}}>
                                <Table stickyHeader aria-label="sticky table">


                                    <TableHead>
                                        <TableRow>
                                            {columns.map((column) => (
                                                <TableCell
                                                    key={column.id}
                                                    align={column.align}
                                                    style={{minWidth: column.minWidth}}
                                                >
                                                    {column.label}
                                                </TableCell>
                                            ))}
                                        </TableRow>
                                    </TableHead>

                                    <TableBody>
                                        {invoiceList?.invoiceList
                                            .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                            .map((row, rowIndex) => {
                                                return (
                                                    <TableRow hover role="checkbox" tabIndex={rowIndex}
                                                              key={row.reference}>
                                                        {columns.map((column, index) => {
                                                            const value = row[column.id];
                                                            if (column.id !== 'action') {
                                                                return (
                                                                    <TableCell key={column.id} align={column.align}
                                                                               id={"cell" + column.id + rowIndex}>
                                                                        {column.format ? column.format(value) : value}
                                                                    </TableCell>
                                                                );
                                                            } else {
                                                                return (
                                                                    <TableCell key={column.id} align={column.align}>
                                                                        <IconButton
                                                                            id={"paymentBtn" + rowIndex}
                                                                            disabled={(row.status === 'PAID' || row.status === 'CANCELLED')}
                                                                            onClick={() => paymentHandling(row.reference)}
                                                                        >
                                                                            <Icon>payment</Icon>
                                                                        </IconButton>
                                                                        <IconButton
                                                                            id={"cancelBtn" + rowIndex}
                                                                            disabled={(row.status === 'PAID' || row.status === 'CANCELLED')}
                                                                            onClick={() => paymentCancelHandling(row.reference)}
                                                                        >
                                                                            <Icon>cancel</Icon>
                                                                        </IconButton>
                                                                    </TableCell>
                                                                );
                                                            }
                                                        })}
                                                    </TableRow>
                                                );
                                            })}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                            <TablePagination
                                rowsPerPageOptions={[10, 25]}
                                component="div"
                                count={invoiceList ? invoiceList?.invoiceList.length : 0}
                                rowsPerPage={rowsPerPage}
                                page={page}
                                onPageChange={handleChangePage}
                                onRowsPerPageChange={handleChangeRowsPerPage}
                            />

                        </CardContent>
                    </Card>

                </Grid>

                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h5" gutterBottom>
                                Payment Summary
                            </Typography>
                            <Grid container spacing={1}>
                                <Grid item xs={3}>
                                    <Typography variant="body1">
                                        Total Amount:
                                    </Typography>
                                </Grid>
                                <Grid item xs={9}>
                                    <Typography variant="body1">
                                        {invoiceList?.invoiceList
                                            .filter(invoice => invoice.status !== 'CANCELLED')
                                            .reduce((accumulator, currentValue) => accumulator + currentValue.amount, 0)} £
                                    </Typography>
                                </Grid>
                                <Grid item xs={3}>
                                    <Typography variant="body1">
                                        Already Paid:
                                    </Typography>
                                </Grid>
                                <Grid item xs={9}>
                                    <Typography variant="body1">
                                        {invoiceList?.invoiceList
                                            .filter(invoice => (invoice.status !== 'CANCELLED' && invoice.status !== 'OUTSTANDING'))
                                            .reduce((accumulator, currentValue) => accumulator + currentValue.amount, 0)} £
                                    </Typography>
                                </Grid>
                                <Grid item xs={3}>
                                    <Typography variant="body1">
                                        Outstanding Amount:
                                    </Typography>
                                </Grid>
                                <Grid item xs={9}>
                                    <Typography variant="body1">
                                        {invoiceList?.invoiceList
                                            .filter(invoice => (invoice.status !== 'CANCELLED' && invoice.status !== 'PAID'))
                                            .reduce((accumulator, currentValue) => accumulator + currentValue.amount, 0)} £
                                    </Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>


        </Box>
        <ErrorDialog open={!!error} onClose={handleCloseErrorDialog} message={error || ''}/>
        <SuccessDialog open={!!success} onClose={handleSuccessDialog} message={success || ''} id={"financeSuccess"}/>
        <ConfirmationDialog open={!!paymentConfirmMessage} message={paymentConfirmMessage || ''}
                            handleClose={closeDialog}
                            handleOk={payApprove} id={"payConfirmation"}/>
        <ConfirmationDialog open={!!paymentCancelConfirmMessage} message={paymentCancelConfirmMessage || ''}
                            handleClose={closeDialog} handleOk={payDeclined} id={"payCancellation"}/>
        <Backdrop
            sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
            open={open}
            onClick={handleClose}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </Layout>
}

export default Finance;