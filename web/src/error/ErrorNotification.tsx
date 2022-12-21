import {useEvent, useStore} from 'effector-react'
import * as React from 'react'
import {$appError, discardAppErrorEvent} from './store'
import {Alert, Snackbar} from '@mui/material'

export const ErrorNotification = () => {
    const error = useStore($appError)
    const discardError = useEvent(discardAppErrorEvent)

    const handleClose = () => discardError()

    return <Snackbar anchorOrigin={{vertical: 'bottom', horizontal: 'left'}}
                     autoHideDuration={8000}
                     open={!!error}
                     onClose={handleClose}
                     sx={{maxWidth: 500}}>
        <Alert severity="error">{error && error.message}</Alert>
    </Snackbar>
}
