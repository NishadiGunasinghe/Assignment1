import React from 'react';
import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Icon, Typography} from "@mui/material";

interface SuccessDialogProps {
    open: boolean;
    onClose: () => void;
    message: string;
    id?: string;
}

const SuccessDialog: React.FC<SuccessDialogProps> = ({open, onClose, message, id}) => {
    return (
        <Dialog open={open} onClose={onClose}>
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
                <Button id={id + "CancelBtn"} onClick={onClose} color="primary">
                    Close
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default SuccessDialog;
