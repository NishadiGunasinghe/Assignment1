import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography} from "@mui/material";
import {BookDto} from "../../services/library/LibraryDto";
import LibraryService from "../../services/library/LibraryService";
import SuccessDialog from "../error/SuccessDialog";
import * as React from "react";


interface ConfirmBookTransactionProps {
    open: boolean
    bookData?: BookDto,
    type: string,
    handleDialog: any
}

const ConfirmBookTransaction = (props: ConfirmBookTransactionProps) => {
    const handleAction = async () => {
        if (props.type === "BORROW" && props.bookData) {
            await LibraryService.borrowBook(props.bookData.isbn);
            setSuccessMessage("Successfully borrowed the book " + props.bookData.title)
        } else if (props.type === "RETURN" && props.bookData) {
            await LibraryService.returnBook(props.bookData.isbn);
            setSuccessMessage("Successfully returned the book " + props.bookData.title)
        }
        handleClose();
    };

    const [success, setSuccessMessage] = React.useState<string | null>(null);
    const handleSuccessDialog = () => {
        setSuccessMessage(null);
        window.location.reload();
    }

    const handleClose = () => {
        props.handleDialog();
    };

    return (
        <>
            <Dialog
                open={props.open}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">{props.bookData?.title}</DialogTitle>
                <DialogContent>
                    <Typography sx={{mb: 1.5}} color="text.secondary">
                        Book Author : {props.bookData?.author} days
                    </Typography>
                    <Typography sx={{mb: 1.5}} color="text.secondary">
                        Year of Published : {props.bookData?.yearOfPublished}
                    </Typography>
                    <Typography variant="body2">
                        Book ISBN Code: {props.bookData?.isbn}
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button id={props.type === "BORROW" ? "borrowBtn" : "returnBtn"} onClick={handleAction}>{props.type === "BORROW" ? "Borrow Book" : "Return Book"}</Button>
                    <Button onClick={handleClose} autoFocus>Cancel</Button>
                </DialogActions>
            </Dialog>
            <SuccessDialog open={!!success} onClose={handleSuccessDialog} message={success || ''} id={props.type === "BORROW" ? "borrow" : "return"}/>
        </>
    );

}

export default ConfirmBookTransaction;