import {Button, Grid, Paper, TextField} from '@mui/material'
import {GlobalIndent} from '../globalStyles'
import {AuthPageStrings, Validation} from '../strings'
import React, {useState} from 'react'
import {logInWithTempUserFx} from './store'

export const AuthPage = () => {
  const [username, setUsername] = useState('')
  const [errorMessage, setErrorMessage] = useState(' ')

  const handleUsernameChange = (newValue: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setErrorMessage(' ')
    setUsername(newValue.target.value)
  }

  const logIn = () => {
    if (username.trim()) {
      logInWithTempUserFx(username.trim())
    } else {
      setErrorMessage(Validation.RequiredField)
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.code === 'Enter') {
      e.preventDefault()
      logIn()
    }
  }

  return (
    <Grid container justifyContent={'center'} alignItems={'center'} spacing={2}
          sx={{padding: GlobalIndent, height: `${100-GlobalIndent}vh`}}
    >
      <Grid item xs={3}>
        <Paper variant={'outlined'} sx={{padding: GlobalIndent}}>
          <Grid container direction={'column'} spacing={GlobalIndent}>
            <Grid item>
              <TextField
                fullWidth
                autoFocus
                error={!!errorMessage.trim()}
                helperText={errorMessage}
                autoComplete={'off'}
                label={AuthPageStrings.UsernamePrompt}
                value={username}
                onChange={handleUsernameChange}
                onKeyDown={handleKeyDown}
              />
            </Grid>
            <Grid item>
              <Button fullWidth variant={'contained'} onClick={logIn}>{AuthPageStrings.LoginButton}</Button>
            </Grid>
          </Grid>
        </Paper>
      </Grid>
    </Grid>
  )
}
