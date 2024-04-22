import React from 'react';
import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Icon, Typography} from "@mui/material";

interface ErrorDialogProps {
    open: boolean;
    onClose: () => void;
    message: string;
    id?: string;
}

const ErrorDialog: React.FC<ErrorDialogProps> = ({open, onClose, message, id}) => {
    return (
        <Dialog open={open} onClose={onClose} id={id}>
            <DialogTitle>
                <Typography variant="h5" align="center" gutterBottom fontWeight={100}
                            style={{display: 'flex', alignItems: 'center', justifyContent: 'center'}}>
                    <span id={id + 'Icon'} style={{color: "#dc4538", marginRight: '5px'}}><Icon
                        fontSize={"large"}>error</Icon></span>
                    <span id={id + 'Title'} style={{color: "#dc4538", marginRight: '5px'}}>ERROR</span>
                </Typography>
            </DialogTitle>
            <DialogContent>
                <Typography id={id + 'Message'}>{message}</Typography>
            </DialogContent>
            <DialogActions>
                <Button id={id + 'BtnClose'} onClick={onClose} color="primary">
                    Close
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ErrorDialog;
