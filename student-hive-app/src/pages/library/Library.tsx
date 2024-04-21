import {JwtDto} from "../../services/auth/AuthDto";
import {
    Backdrop,
    Box, CircularProgress,
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
import React, {useEffect, useState} from "react";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import {BookDto, BookDtos} from "../../services/library/LibraryDto";
import ErrorDialog from "../../components/error/ErrorDialog";
import LibraryService from "../../services/library/LibraryService";
import ConfirmBookTransaction from "../../components/confirmbooktransaction/ConfirmBookTransaction";

interface Column {
    id: 'isbn' | 'title' | 'author' | 'yearOfPublished' | 'action';
    label: string;
    minWidth?: number;
    align?: 'right';
    format?: (value: any) => any;
}

const columns: readonly Column[] = [
    {
        id: 'isbn',
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
        id: 'yearOfPublished',
        label: 'Year Of Published',
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

interface LibraryProps {
    jwtPayload: JwtDto
}

const Library = (props: LibraryProps) => {

    let [bookList, setBooks] = useState<BookDtos>();

    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    const [confirmationStatus, setConfirmationStatus] = React.useState({
        open: false,
        type: ""
    });
    const [confirmationData, setConfirmationData] = React.useState<BookDto>();

    function actionHandling(bookDto: BookDto, type: string) {
        setConfirmationStatus({
            open: true,
            type: type
        });
        setConfirmationData(bookDto);
    }

    function handleConfirmationDialog() {
        setConfirmationStatus({
            open: false,
            type: ""
        });
    }

    const [open, setOpen] = React.useState(false);
    const handleClose = () => {
        setOpen(false);
    };
    const handleToggle = () => {
        setOpen(!open);
    };

    const [error, setError] = useState<string | null>(null);
    const handleCloseErrorDialog = () => {
        setError(null);
    };

    useEffect(() => {
        handleToggle()
        const fetchData = async () => {
            try {
                const bookData = await LibraryService.getAllBooks();
                setBooks(bookData)
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

    return (<Layout>
        <Box sx={{bgcolor: '#ffffff', padding: 2}}>

            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Grid item>
                        <Typography variant="h4">Books</Typography>
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
                                    {bookList?.books
                                        .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                        .map((row) => {
                                            return (
                                                <TableRow hover role="checkbox" tabIndex={-1} key={row.isbn}>
                                                    {columns.map((column) => {
                                                        const value = row[column.id];
                                                        if (column.id !== 'action') {
                                                            return (
                                                                <TableCell key={column.id} align={column.align}>
                                                                    {column.format ? column.format(value) : value}
                                                                </TableCell>
                                                            );
                                                        } else {
                                                            if (row.isBorrowed) {
                                                                return (
                                                                    <TableCell key={column.id} align={column.align}>
                                                                        <IconButton
                                                                            id={"returnBtn" + row.isbn}
                                                                            onClick={() => actionHandling(row, "RETURN")}
                                                                        >
                                                                            <Icon>repartition</Icon>
                                                                        </IconButton>
                                                                    </TableCell>
                                                                );
                                                            } else {
                                                                return (
                                                                    <TableCell key={column.id} align={column.align}>
                                                                        <IconButton
                                                                            id={"borrowBtn" + row.isbn}
                                                                            onClick={() => actionHandling(row, "BORROW")}
                                                                        >
                                                                            <Icon>library_add</Icon>
                                                                        </IconButton>
                                                                    </TableCell>
                                                                );
                                                            }
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
                            count={bookList ? bookList?.books.length : 0}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />

                    </CardContent>
                </Card>
            </Grid>
        </Box>
        <ConfirmBookTransaction open={confirmationStatus.open} bookData={confirmationData}
                                type={confirmationStatus.type} handleDialog={handleConfirmationDialog}/>
        <ErrorDialog open={!!error} onClose={handleCloseErrorDialog} message={error || ''}/>
        <Backdrop
            sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
            open={open}
            onClick={handleClose}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </Layout>);
}

export default Library;