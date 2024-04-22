import {JwtDto} from "../../services/auth/AuthDto";
import {
    Box,
    Divider,
    Grid, Icon, IconButton,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead, TablePagination,
    TableRow,
    Typography
} from "@mui/material";
import Layout from "../../layout/Layout";
import React, {useState} from "react";
import {TransactionDtos} from "../../services/library/LibraryDto";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";

interface Column {
    id: 'bookIsbn' | 'title' | 'author' | 'dateBorrowed' | 'dateReturned' | 'action';
    label: string;
    minWidth?: number;
    align?: 'right';
    format?: (value: any) => any;
}

const columns: readonly Column[] = [
    {
        id: 'bookIsbn',
        label: 'ISBN',
        minWidth: 100,
        align: 'right',
        format: (value: any) => value.toLocaleString('en-US'),
    },
    {
        id: 'title',
        label: 'Book Title',
        minWidth: 100,
        align: 'right',
        format: (value: any) => value.toLocaleString('en-US'),
    },
    {
        id: 'author',
        label: 'Author',
        minWidth: 100,
        align: 'right',
        format: (value: any) => value.toLocaleString('en-US'),
    },
    {
        id: 'dateBorrowed',
        label: 'Date Borrowed',
        minWidth: 100,
        align: 'right',
        format: (value: any) => value.toLocaleString('en-US'),
    },
    {
        id: 'dateReturned',
        label: 'Date Returned',
        minWidth: 100,
        align: 'right',
        format: (value: any) => value.toLocaleString('en-US'),
    },
    {
        id: 'action',
        label: 'Action',
        minWidth: 100,
        align: 'right'
    }
];


interface ViewAllBorrowedBooksProps {
    jwtPayload: JwtDto
}

const ViewAllBorrowedBooks = (props: ViewAllBorrowedBooksProps) => {

    let [transactionList, setTransactions] = useState<TransactionDtos>();

    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    function retunedBookHandling(isbn: string) {
        alert("retuned the book " + isbn);
    }


    return (<Layout>
        <Box sx={{bgcolor: '#ffffff', padding: 2}}>

            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Grid item>
                        <Typography variant="h4">Books Borrowed</Typography>
                    </Grid>
                    <Divider sx={{
                        marginTop: '25px', // Adjust the top margin as needed
                        marginBottom: '25px', // Adjust the bottom margin as needed
                    }}/>
                </Grid>
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
                                    {transactionList?.transactions
                                        .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                        .map((row) => {
                                            return (
                                                <TableRow hover role="checkbox" tabIndex={-1} key={row.bookIsbn}>
                                                    {columns.map((column) => {
                                                        const value = row[column.id];
                                                        if (column.id !== 'action') {
                                                            return (
                                                                <TableCell key={column.id} align={column.align}>
                                                                    {column.format ? column.format(value) : value}
                                                                </TableCell>
                                                            );
                                                        } else {
                                                            return (
                                                                <TableCell key={column.id} align={column.align}>
                                                                    <IconButton
                                                                        disabled={!row.dateReturned}
                                                                        onClick={() => retunedBookHandling(row.bookIsbn)}
                                                                    >
                                                                        <Icon>payment</Icon>
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
                            count={transactionList ? transactionList?.transactions.length : 0}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />

                    </CardContent>
                </Card>

            </Grid>

        </Box>
    </Layout>);
}

export default ViewAllBorrowedBooks;