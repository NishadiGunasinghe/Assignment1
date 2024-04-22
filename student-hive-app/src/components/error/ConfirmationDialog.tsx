import React from 'react';
import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Icon, Typography} from "@mui/material";

interface ConfirmationDialogProp {
    open: boolean;
    handleOk: () => void;
    handleClose: () => void;
    message: string;
    id?: string
}


const ConfirmationDialog: React.FC<ConfirmationDialogProp> = ({open, handleOk, handleClose, message, id}) => {
    return (
        <Dialog open={open} onClose={handleClose}>
            <DialogTitle>
                <Typography variant="h5" align="center" gutterBottom fontWeight={100}
                            style={{display: 'flex', alignItems: 'center', justifyContent: 'center'}}>
                    <span style={{color: "#207221", marginRight: '5px'}}><Icon
                        fontSize={"large"}>check_circle</Icon></span>
                    <span style={{color: "#207221", marginRight: '5px'}}>Success</span>
                </Typography>
            </DialogTitle>
            <DialogContent>
                <Typography id={id + "Message"}>{message}</Typography>
            </DialogContent>
            <DialogActions>
                <Button id={id + "OkBtn"} onClick={handleOk} color="primary">
                    Ok
                </Button>
                <Button id={id + "CloseBtn"} onClick={handleClose} color="primary">
                    Close
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ConfirmationDialog;